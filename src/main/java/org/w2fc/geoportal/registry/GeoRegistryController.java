package org.w2fc.geoportal.registry;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.geoportal.dao.GeoLayerDao;
import org.w2fc.geoportal.domain.AddnsRating;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoLayerMetadata;
import org.w2fc.geoportal.domain.GeoLayerType;
import org.w2fc.geoportal.layer.GeoLayerUIAdapter;
import org.w2fc.geoportal.layer.LayerController;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.spring.AbstractController;

@Controller
@RequestMapping(value = "/registry")
public class GeoRegistryController extends AbstractController<GeoLayer, GeoLayerDao, Long> {

    @Autowired
    ServiceRegistry serviceRegistry;
    
    @Autowired
    LayerController layerController;
    
    @Override
    @Autowired
    public void setAutowiredDao(GeoLayerDao dao) {
        setDao(dao);
    }

    
    @RequestMapping(value = "/")
    @Transactional(readOnly = true)
    public String getTemplateEditor(Model model) {
        return "markup/registry";
    }

    
    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public @ResponseBody List<GeoLayer>  getListServices() {
        return getDao().listRegistryServices();
    }

    
    @RequestMapping(value = "/layers", method = RequestMethod.GET)
    public @ResponseBody List<GeoLayer> getLayersList() {
        return getDao().listRegistryLayers();
    }
    
    
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody GeoLayerUIAdapter updateRegistryEntry(GeoLayerUIAdapter object) {
        Long id = layerController.createOrUpdateLayer(object);
        return new GeoLayerUIAdapter(getById(id));
    }
    
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getRegistryEntry(@PathVariable Long id, @RequestParam(required=false) String type, Model model) {
        GeoLayer layer = getById(id);

        if(null == layer){
            layer = new GeoLayer();
            layer.setMetadata(new GeoLayerMetadata());
//            layer.getMetadata().setRegistry(true);

            if("services".equalsIgnoreCase(type)){
                layer.setTypeId(GeoLayerType.TYPE_REGISTRY_SERVICE);
            }else{
                layer.setTypeId(GeoLayerType.TYPE_REGISTRY_LAYER);
            }
        }

        model.addAttribute("portalLayer", new GeoLayerUIAdapter(layer));

        AddnsRating rating = serviceRegistry.getRatingDao().get(layer.getId());
        model.addAttribute("rating", null != rating ? rating.getClicks() : 0);
        model.addAttribute("likes",  null != rating ? rating.getLikes() : 0);
        GeoLayerType layerType = serviceRegistry.getLayerTypeDao().get(layer.getTypeId());
        model.addAttribute("title", null != layerType ? layerType.getName() : "не определенного типа");

        return "markup/layer/LayerEditStandalone";
    }
}
