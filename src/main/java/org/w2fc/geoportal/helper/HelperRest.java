package org.w2fc.geoportal.helper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.conf.Constants;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.dao.SearchDao;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.gis.GeoObjectUI;
import org.w2fc.geoportal.utils.ServiceRegistry;

@Controller
@RequestMapping(value = "/helper")
public class HelperRest {
	final static Logger logger = LoggerFactory.getLogger(HelperRest.class);

	@Autowired
	private ServiceRegistry serviceRegistry;
	
	@Autowired
	private SearchDao searchDao;
	
	@RequestMapping("/")
	public String getHelper(Model model) {
		return "markup/helper/Helper";
	}

	@RequestMapping(value = "/search", method = { RequestMethod.POST,
			RequestMethod.GET })
	@Transactional
	public  @ResponseBody
	List<GeoObjectUI> search(@RequestParam String q, @RequestParam Integer m, @RequestParam Integer p, @RequestParam(required = false) String fias, @RequestParam(required = false) Double lat, @RequestParam(required = false) Double lng) {
		List<GeoLayer> layers;
		if(Constants.isAdministrator()){
            layers = serviceRegistry.getLayerDao().list();        
        }else{
        	layers = serviceRegistry.getLayerDao().listTreeLayers(
                    serviceRegistry.getUserDao().getCurrentGeoUser().getId());
        }
		List<Long> layersIds = new ArrayList<Long>(); 
		for(GeoLayer l: layers){
			if(l.getTypeId() == 1)layersIds.add(l.getId());
		}
		
        if(m == 2){
			List<GeoObject> searchResult = serviceRegistry.getGeoObjectDao().getByPointOrFias(lat, lng, fias, layersIds);
			//List<GeoObject> searchResult = serviceRegistry.getGeoObjectDao().getByFias(fias);
			if(searchResult != null && searchResult.size() > 0){
				return ObjectFactory.createGeoObjectUIAdapterListLite(searchResult);
			}else{
				return new ArrayList<GeoObjectUI>();
			}
		}
		
		List<GeoObject> geoObjList = searchDao.search(q, GeoObject.class);
		List<GeoObject> resultList = new ArrayList<GeoObject>();
		for(GeoObject obj: geoObjList){
			for(GeoLayer l : obj.getGeoLayers()){
				if(layers.contains(l)){
					resultList.add(obj);
					break;
				}
			}
		}
		
        return ObjectFactory.createGeoObjectUIAdapterListLite(resultList);
	}

	
	@RequestMapping(value = "/index")
	@PreAuthorize("@geoportalSecurity.isAdministrator()")
	@Transactional
	public @ResponseBody String  index() throws InterruptedException {
		searchDao.index();
		return "Success";
	}
	
}
