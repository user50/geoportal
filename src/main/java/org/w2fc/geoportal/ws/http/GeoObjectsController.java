package org.w2fc.geoportal.ws.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.w2fc.geoportal.ws.PortalWsService;
import org.w2fc.geoportal.ws.model.*;

import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Yevhen
 */
@Controller
@RequestMapping("/geoobjects")
public class GeoObjectsController {

    @Autowired
    private PortalWsService portalWsService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET,  produces = "application/json")
    @ResponseBody
    public String ping(){
        return "passed";
    }

    @RequestMapping(method = RequestMethod.GET,  produces = "application/json")
    @ResponseBody
    public GetObjectsResponse listByLayer(@RequestParam Long layerId){
        return portalWsService.getObjects(layerId);
    }

    @RequestMapping(value = "/partial",method = RequestMethod.GET,  produces = "application/json")
    @ResponseBody
    public GetObjectsResponse getObjectsPartial(@RequestParam Long layerId,
                                                @RequestParam Long start,
                                                @RequestParam Long count){
        return portalWsService.getObjectsPartial(layerId, start, count);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,  produces = "application/json")
    @ResponseBody
    public GeoObjectFullAdapter get(@PathVariable Long id){
        return portalWsService.getObject(id);
    }

    @RequestMapping(value = "/points", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Long createPoint(@RequestBody RequestPoint requestPoint){
        return portalWsService.createPoint(requestPoint);
    }

    @RequestMapping(value = "/lines", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Long createLine(@RequestBody RequestLine requestLine){
        return portalWsService.createLine(requestLine);
    }

    @RequestMapping(value = "/polygons", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Long createPolygon(@RequestBody RequestPolygon requestPolygon){
        return portalWsService.createPolygon(requestPolygon);
    }

    @RequestMapping(value = "/points/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public void updatePoint(@PathVariable Long id, @RequestBody RequestPoint requestPoint){
        portalWsService.updatePoint(id, requestPoint);
    }

    @RequestMapping(value = "/lines/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public void updateLine(@PathVariable Long id, @RequestBody RequestLine requestLine){
        portalWsService.updateLine(id, requestLine);
    }

    @RequestMapping(value = "/polygons/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public void updatePolygon(@PathVariable Long id, @RequestBody RequestPolygon requestPolygon){
        portalWsService.updatePolygon(id, requestPolygon);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public void delete(@PathVariable Long id, @RequestParam Long layerId){
        portalWsService.delete(layerId, id);
    }

}
