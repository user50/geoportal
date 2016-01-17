package org.w2fc.geoportal.ws.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.w2fc.geoportal.dao.GeoObjectDao;
import org.w2fc.geoportal.ws.GeoObjectsService;
import org.w2fc.geoportal.ws.model.*;

/**
 * @author Yevhen
 */
@Controller
@RequestMapping("/geoobjects")
public class GeoObjectsController {

    @Autowired
    private GeoObjectDao geoObjectDao;

    @Autowired
    private GeoObjectsService geoObjectsService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,  produces = "application/json")
    @ResponseBody
    @Transactional(readOnly=true)
    public GeoObjectFullAdapter get(@PathVariable Long id){
        return new GeoObjectFullAdapter(geoObjectDao.get(id));
    }

    @RequestMapping(value = "/points", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    @Transactional
    public Long createPoint(@RequestBody RequestPoint requestPoint){
        return geoObjectsService.createPoint(requestPoint);
    }

    @RequestMapping(value = "/lines", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    @Transactional
    public Long createLine(@RequestBody RequestLine requestLine){
        return geoObjectsService.createLine(requestLine);
    }

    @RequestMapping(value = "/polygons", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    @Transactional
    public Long createPolygon(@RequestBody RequestPolygon requestPolygon){
        return geoObjectsService.createPolygon(requestPolygon);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    @Transactional
    public void delete(@PathVariable Long id, @RequestParam Long layerId){
        geoObjectsService.delete(layerId, id);
    }

}
