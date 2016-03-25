package org.w2fc.geoportal.ws.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.ws.PortalWsService;
import org.w2fc.geoportal.ws.model.GetLayersResponse;

/**
 * @author Yevhen
 */
@Controller
@RequestMapping("/layers")
public class GeoLayersController {

    @Autowired
    private PortalWsService portalWsService;

    @RequestMapping(method = RequestMethod.GET,  produces = "application/json")
    @ResponseBody
    public GetLayersResponse getLayers(){
        return portalWsService.getLayers();
    }

    @RequestMapping(value = "/{layerId}/count",method = RequestMethod.GET,  produces = "application/json")
    @ResponseBody
    public Long getLayerObjectsCount(@PathVariable Long layerId){
        return portalWsService.getObjectsCount(layerId);
    }
}
