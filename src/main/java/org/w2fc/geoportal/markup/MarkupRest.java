package org.w2fc.geoportal.markup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value = "/markup")
public class MarkupRest {
    
    
    @RequestMapping(value="/{name}")
    public String getMarkup(@PathVariable String name) {
        return "markup/" + name;
    }
    
    
    @RequestMapping(value="/{subdir}/{name}")
    public String getMarkup(@PathVariable String subdir, @PathVariable String name) {
        return "markup/" + subdir + "/" + name;
    }
}
