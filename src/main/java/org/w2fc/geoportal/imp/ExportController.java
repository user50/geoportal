package org.w2fc.geoportal.imp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.gis.GeoObjectUI;
import org.w2fc.geoportal.utils.CoordinateTransformer;
import org.w2fc.geoportal.utils.ServiceRegistry;


@Controller
@RequestMapping(value = "/export")
public class ExportController {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
	private CoordinateTransformer transformer;

    @RequestMapping(value = "/layer/")
    @Transactional(readOnly = true)
    public void export(
            @RequestParam("id") Long layerId, 
            @RequestParam String format,
            @RequestParam String srs,
            HttpServletResponse response) throws Exception {
        
        //GeoLayer layer = serviceRegistry.getLayerDao().get(layerId);
        
    	List<GeoObject> objects = transformer.toSRS(serviceRegistry.getGeoObjectDao().listByLayerIdPropertiesFetched(layerId), srs);
    	
        List<GeoObjectUI> objectByTagsME = ObjectFactory.createGeoObjectUIAdapterList(objects);
        
        Set<String> keys = new TreeSet<String>();

        for (GeoObjectUI mapEntry : objectByTagsME) {
            List<Map<String, String>> tags1 = mapEntry.getTags();
            for (Map<String, String> tag : tags1) {
                keys.add(tag.get("key"));
            }
        }
        
        File[] files = {};
        
        File tmpDir = ImportUtils.createTempDir();
        
        if("MIFMID".equalsIgnoreCase(format)){
            files = ExportUtils.exportAsMifMid(objectByTagsME, keys, tmpDir);
            
        }else if("CSV".equalsIgnoreCase(format)){
            files = ExportUtils.exportAsCsv(objectByTagsME, keys, tmpDir, "csv");
            
        }else{
            files = ExportUtils.exportAsCsv(objectByTagsME, keys, tmpDir, "txt");
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(out);

        for (int i = 0; i < files.length; i++) {
            ZipEntry ze= new ZipEntry(files[i].getName());
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(files[i]);
            
            int len;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            files[i].delete();
        }
        
        // close zip streams
        zos.closeEntry();
        zos.close();
        
        // delete temporary directory
        tmpDir.delete();
        
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment; filename=export.zip");
        out.writeTo(response.getOutputStream());
        response.flushBuffer();
        
        // close buffer
        out.close();
    }
    
    @RequestMapping(value = "/dialog")
    public String getDialog(Model model){
		
		model.addAttribute("msk", serviceRegistry.getReferenceSystemProjDao().list());
		return "markup/layer/Export";
	}
}
