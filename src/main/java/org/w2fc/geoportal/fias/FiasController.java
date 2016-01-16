package org.w2fc.geoportal.fias;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.geoportal.fias.FiasDaoImpl.AddressObject;


@Controller
@RequestMapping(value = "/fias")
public class FiasController {
    
    @Autowired
    FiasDaoImpl fiasDaoImpl;

    @Cacheable("GeoPortalCache")
    @RequestMapping(value = "/searchFullAddress", method = RequestMethod.GET)
    public @ResponseBody List<List<AddressObject>>  search(@RequestParam String term) {
        return fiasDaoImpl.search(term);
    }
    
    @RequestMapping(value = "/searchObject", method = RequestMethod.GET)
    public @ResponseBody List<AddressObject>  search(@RequestParam String q, @RequestParam int levelLow,  @RequestParam int levelHigh) {
        return fiasDaoImpl.search(q, levelLow, levelHigh);
    }
}
