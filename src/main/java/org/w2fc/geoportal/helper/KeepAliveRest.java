package org.w2fc.geoportal.helper;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class KeepAliveRest {
    @RequestMapping(value="/keepAlive")
    public @ResponseBody boolean keepAlive(@RequestParam String userName) {
        if(userName.equalsIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName())){
            return true;
        }
        
        return false;
    }
}
