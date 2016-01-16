package org.w2fc.geoportal.imp;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.domain.GeoSettings;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.user.CustomUserDetails;
import org.w2fc.geoportal.utils.CoordinateTransformer;
import org.w2fc.geoportal.utils.ServiceRegistry;

import com.vividsolutions.jts.geom.Geometry;


@Controller
@RequestMapping(value = "/import")
public class ImportController {
    
	final static Logger logger = LoggerFactory.getLogger(ImportController.class);
    
	protected static final String IMPORT_STATUS = "importStatus";

	@Autowired
	private ServiceRegistry serviceRegistry;
    
	@Autowired
	private PlatformTransactionManager txManager;
	
	@Autowired
	private CoordinateTransformer transformer;
	
	public static class ResponseStatus{
		Integer total;
		Integer loaded;
		public Integer getTotal() {
			return total;
		}
		public void setTotal(Integer total) {
			this.total = total;
		}
		public Integer getLoaded() {
			return loaded;
		}
		public void setLoaded(Integer loaded) {
			this.loaded = loaded;
		}
	}
	
	@RequestMapping(value = "/dialog")
    public String getDialog(Model model){
		
		model.addAttribute("msk", serviceRegistry.getReferenceSystemProjDao().list());
		return "markup/layer/Import";
	}
	

    @RequestMapping(value = "/layer/")
    @Transactional
    public @ResponseBody ResponseStatus list(
            @RequestParam("id") Long layerId, 
            @RequestParam("file") MultipartFile file,
            @RequestParam("ref") String refKey,
            HttpServletRequest request) throws Exception {

        String[] nameTokens = file.getOriginalFilename().split("\\.(?=[^\\.]+$)");
        InputStream is = file.getInputStream();

        ResponseStatus resp = null;
        
        try{
            if("XML".equalsIgnoreCase(nameTokens[1])){
            	resp = saveToLayer(layerId, transformer.toWGS(ImportUtils.fromXml(is), refKey), request.getSession());

            }else if("TXT".equalsIgnoreCase(nameTokens[1])){
            	resp = saveToLayer(layerId, transformer.toWGS(ImportUtils.fromCsv(is), refKey), request.getSession());
            
            }else if("XLS".equalsIgnoreCase(nameTokens[1])){
            	resp = saveToLayer(layerId, transformer.toWGS(ImportUtils.fromXls(is), refKey), request.getSession());
            
            }else if("CSV".equalsIgnoreCase(nameTokens[1])){
            	resp = saveToLayer(layerId, transformer.toWGS(ImportUtils.fromCsv(is), refKey), request.getSession());
                
            }else if("ZIP".equalsIgnoreCase(nameTokens[1])){
            	resp = saveToLayer(layerId, transformer.toWGS(processZip(file), refKey), request.getSession());
                
            }else if("GPX".equalsIgnoreCase(nameTokens[1])){
            	resp = saveToLayer(layerId, ImportUtils.fromGpx(nameTokens[0], is), request.getSession());
                
            }else if("JPG".equalsIgnoreCase(nameTokens[1])){
            	List<GeoObject> list = ImportUtils.fromJPG(nameTokens[0], is);
            	if(list.size() > 0){
            		String fileName = saveJPGToFolder(file);
            		if(fileName != null){
            			Set<GeoObjectTag> tags = list.get(0).getTags();
            			tags.add(ObjectFactory.createGeoObjectTag(list.get(0), "img", createJPGUrl(fileName)));
            		}
            	}
            	resp = saveToLayer(layerId, list , request.getSession());
                
            }else{
                return resp;
            }
            
        }finally{
            is.close();
        }
        request.getSession().setAttribute(IMPORT_STATUS,resp);
        return resp;
    }
    
    
    private String createJPGUrl(String fileName) {
    	List<GeoSettings> settings = serviceRegistry.getGeoSettingsDao().list();
		String url = "http://localhost/";
		for(GeoSettings setting : settings){
			if(setting.getKey().equals("JPG_URL")){
				url = setting.getValue();
			}
		}
		return url + fileName;
	}


	private String saveJPGToFolder(MultipartFile file) {
		List<GeoSettings> settings = serviceRegistry.getGeoSettingsDao().list();
		String folder = "";
		for(GeoSettings setting : settings){
			if(setting.getKey().equals("JPG_FOLDER")){
				folder = setting.getValue();
			}
		}
		SimpleDateFormat dt = new SimpleDateFormat("yyyyymmdd"); 
		String date = dt.format(Calendar.getInstance().getTime());
		
		String fileNameToCreate = folder + date+ "_" + file.getOriginalFilename();
		try {
			File newFile = new File(fileNameToCreate);
			FileUtils.writeByteArrayToFile(newFile, file.getBytes());

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
		return date+ "_" + file.getOriginalFilename();
	}


	@RequestMapping(value = "/layerstatus/")
    public @ResponseBody ResponseStatus status(HttpServletRequest request){
    	return (ResponseStatus) request.getSession().getAttribute(IMPORT_STATUS);
    }
    
    private List<GeoObject> processZip(MultipartFile file) throws Exception {
        File tmpZipFile = File.createTempFile("tmpImportZip" + System.currentTimeMillis() + ".zip", null);
        file.transferTo(tmpZipFile);

        File tempDir = ImportUtils.createTempDir();
        List<File> tmpFiles = ImportUtils.extractZip(tmpZipFile, tempDir);

        // read data
        List<GeoObject> res = new LinkedList<GeoObject>();

        try{

            String[] filesByExt = searchByExt(tempDir, ".mif");
            if(0 < filesByExt.length){
                for (String mifFile : filesByExt) {
                    res.addAll(ImportUtils.fromMifMid(tempDir.getAbsolutePath() + "/" +mifFile));
                }
            }

            filesByExt = searchByExt(tempDir, ".dbf");
            if(0 < filesByExt.length){
                for (String mifFile : filesByExt) {
                    res.addAll(ImportUtils.fromDbf(tempDir.getAbsolutePath() + "/" +mifFile));
                }
            }     

        }finally{
            // clear temporary data
            for (File file2 : tmpFiles) {
                file2.delete();
            }

            tempDir.delete();
            tmpZipFile.delete();
        }
        return res;
    }


    private String[] searchByExt(File tempDir, final String fileExtention) {
        String[] mifFiles = tempDir.list(new FilenameFilter() {
            public boolean accept(File directory, String fileName){
                return fileName.toUpperCase().endsWith(fileExtention.toUpperCase());
            }
        });
        return mifFiles;
    }


    private ResponseStatus saveToLayer(final Long layerId, final List<GeoObject> list, final HttpSession session) {
        
        
        
        ResponseStatus result = new ResponseStatus();
        
        result.setTotal(list.size());
        result.setLoaded(0);
        
        final GeoUser currentUser = serviceRegistry.getUserDao().getCurrentGeoUser();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object user = auth.getPrincipal();
		final Geometry permArea;
    	if(user instanceof CustomUserDetails){
    		if(((CustomUserDetails)user).getPermissionArea() != null){
    			permArea = ((CustomUserDetails)user).getPermissionArea().get("area");
    		}else{
    			permArea = null;
    		}
    	}else{
    		permArea = null;
    	}
        
        Thread importThread = new Thread(){
        	@Override
        	public void run() {
        		super.run();
        		TransactionStatus tx = txManager.getTransaction(new DefaultTransactionDefinition());
        		GeoLayer l = serviceRegistry.getLayerDao().get(layerId);
        		try{
        		ResponseStatus status = (ResponseStatus) session.getAttribute(IMPORT_STATUS);
        		for (int i = 0 ; i < list.size(); i++) {
        			GeoObject co = list.get(i);
        			if(!checkArea(permArea, co))continue;
                    Set<GeoLayer> layers = co.getGeoLayers();
                    if(layers == null){
                    	layers = new HashSet<GeoLayer>();
                    	co.setGeoLayers(layers);
                    }
                    layers.add(l);
                    l.getGeoObjects().add(co);
                    co.setCreatedBy(currentUser);
    				co.setCreated(Calendar.getInstance().getTime());
                    serviceRegistry.getGeoObjectDao().add(co);
                    if(status != null){
                    	status.setLoaded(i);
                    	session.setAttribute(IMPORT_STATUS, status);
                    }
                }
        		txManager.commit(tx);
        		if(status != null){
                	status.setLoaded(list.size());
                	session.setAttribute(IMPORT_STATUS, status);
                }
        		}catch(Exception e){
        			logger.error(e.getLocalizedMessage(), e);
        			txManager.rollback(tx);
        			ResponseStatus status = (ResponseStatus) session.getAttribute(IMPORT_STATUS);
        			status.setLoaded(-1);
                	session.setAttribute(IMPORT_STATUS, status);
        		}
        	}
        };
        
        importThread.start();
        
        return result;
    }


    public static void print(List<GeoObject> res) {
        for (GeoObject co : res) {
            System.out.println(co.getName());
            System.out.println(co.getTheGeom());
             Set<GeoObjectTag> p = co.getTags();
            for (GeoObjectTag tag : p) {
                System.out.println(tag.getKey() + " " + tag.getValue());
            }
        }
    }
    
    private boolean checkArea(Geometry permArea, GeoObject gisObject) {
    	if(permArea == null)return true;
    	if(permArea.contains(gisObject.getTheGeom())){
    		return true;
    	}else{
    		logger.warn("Ошибка! Территория не доступна для редактирования.");
    		return false;
    	}
	}
    
}
