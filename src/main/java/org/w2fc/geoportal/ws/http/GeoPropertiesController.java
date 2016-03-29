package org.w2fc.geoportal.ws.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/geoobjects")
public class GeoPropertiesController {

    @Autowired
    private GeoObjectDrawPropertiesService service;

    @RequestMapping(value = "/{id}/properties", method = RequestMethod.POST)
    @ResponseBody
    public Long update(@PathVariable Long id, @RequestBody GeoObjectDrawProperties drawProperties){
        service.save(id, drawProperties);
        return id;
    }
}
