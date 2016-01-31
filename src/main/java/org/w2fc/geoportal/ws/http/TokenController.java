package org.w2fc.geoportal.ws.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.conf.Constants;
import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.domain.GeoUser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Yevhen
 */
@Controller
@RequestMapping("/security/token")
public class TokenController {

    @Autowired
    @Qualifier(value = "tokenizedGeoUserDao")
    private GeoUserDao geoUserDao;

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @Transactional(readOnly = true)
    public String getToken(HttpServletResponse response) throws IOException {
        String token = geoUserDao.getCurrentGeoUser().getToken();
        if (token == null || Constants.isAnonymous()){
            response.setStatus(401);
            response.getWriter().write("Access denied");
            return null;
        } else
            return token;
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @Transactional
    public String generateToken(HttpServletResponse response) throws IOException {
        GeoUser geoUser = geoUserDao.getCurrentGeoUser();
        if (geoUser == null || Constants.isAnonymous()){
            response.setStatus(401);
            response.getWriter().write("Access denied");
            return null;
        } else {
            String newToken = UUID.randomUUID().toString();
            geoUser.setToken(newToken);
            geoUserDao.update(geoUser);

            return newToken;
        }
    }
}
