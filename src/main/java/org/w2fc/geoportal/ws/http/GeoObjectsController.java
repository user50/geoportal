package org.w2fc.geoportal.ws.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.w2fc.geoportal.config.ThreadPids;
import org.w2fc.geoportal.ws.PortalWsService;
import org.w2fc.geoportal.ws.model.*;

import javax.jws.WebMethod;
import javax.servlet.http.HttpSession;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

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
    public Long createPoint(@RequestBody RequestPoint requestPoint, HttpSession session){
        ThreadPids.INSTANCE.put(Thread.currentThread().getId(), session.getId());
        return portalWsService.createPoint(requestPoint);
    }

    @RequestMapping(value = "/lines", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Long createLine(@RequestBody RequestLine requestLine, HttpSession session){
        ThreadPids.INSTANCE.put(Thread.currentThread().getId(), session.getId());
        return portalWsService.createLine(requestLine);
    }

    @RequestMapping(value = "/polygons", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Long createPolygon(@RequestBody RequestPolygon requestPolygon, HttpSession session){
        ThreadPids.INSTANCE.put(Thread.currentThread().getId(), session.getId());
        return portalWsService.createPolygon(requestPolygon);
    }

    @RequestMapping(value = "/points/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Long updatePoint(@PathVariable Long id, @RequestBody RequestPoint requestPoint, HttpSession session){
        ThreadPids.INSTANCE.put(Thread.currentThread().getId(), session.getId());
        portalWsService.updatePoint(id, requestPoint);
        return id;
    }

    @RequestMapping(value = "/lines/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Long updateLine(@PathVariable Long id, @RequestBody RequestLine requestLine, HttpSession session){
        ThreadPids.INSTANCE.put(Thread.currentThread().getId(), session.getId());
        portalWsService.updateLine(id, requestLine);
        return id;
    }

    @RequestMapping(value = "/polygons/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Long updatePolygon(@PathVariable Long id, @RequestBody RequestPolygon requestPolygon, HttpSession session){
        ThreadPids.INSTANCE.put(Thread.currentThread().getId(), session.getId());
        portalWsService.updatePolygon(id, requestPolygon);
        return id;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public void delete(@PathVariable Long id, @RequestParam Long layerId, HttpSession session){
        ThreadPids.INSTANCE.put(Thread.currentThread().getId(), session.getId());
        portalWsService.delete(id);
    }

    @RequestMapping(value = "/srs", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<String> getSpatialRefSystems(){
        return portalWsService.getSpatialRefSystems();
    }
}
