package org.w2fc.geoportal.integration.ru.infor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.conf.Constants;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.utils.ServiceRegistry;


@Controller
@RequestMapping(value = "/infor")
public class MigrationController {

    @Autowired
    ServiceRegistry serviceRegistry;
    
    @Autowired
    MigrationDao migrationDao;

    
    @RequestMapping(method = RequestMethod.GET)
    public String getPage(@RequestParam(required = false) String layerId, Model model) {
        model.addAttribute("layerId", layerId);

        List<GeoLayer> list = null;
        if(Constants.isAdministrator()){
            list = serviceRegistry.getLayerDao().list();
        }else{
            list = serviceRegistry.getLayerDao()
                    .listTreeLayersEditable(serviceRegistry.getUserDao().getCurrentGeoUser().getId());
        }
        Collections.sort(list, new LayerByNameComp());
        model.addAttribute("layers", list);
        return "markup/infor";
    }
    
    
    public static class SearchResults{
        public List<GeoObject> listLast1000 = null;
        public List<String> ids = new LinkedList<String>();

        public List<GeoObject> getListLast1000() {
            return listLast1000;
        }

        public List<String> getIds() {
            return ids;
        }
    }
    
    @RequestMapping(value = "/search")
    public @ResponseBody SearchResults searchObjectsByTags(@RequestBody List<String> tags) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
        
        List<GeoObject> list = migrationDao.getObjectsByTags(tags);
        
        SearchResults res = new SearchResults();
        
        final int LIMIT = 1000;
        if(LIMIT < list.size()){
            res.listLast1000 = list.subList(0, LIMIT);
        }else{
            res.listLast1000 = list;
        }
        
        for (GeoObject geoObject : list) {
            res.ids.add(((String) geoObject.getClass().getMethod("getPrefix").invoke(geoObject)) + geoObject.getId());
        }
        
        return res;
    }
    
    
    @RequestMapping(value = "/import/{layerId}")
    public @ResponseBody Boolean importByIds(@PathVariable Long layerId, @RequestBody List<String> objIds) {
        return migrationDao.importByIds(layerId, objIds);
    }
}
