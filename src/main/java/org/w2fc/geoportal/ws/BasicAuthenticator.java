package org.w2fc.geoportal.ws;

import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.util.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.w2fc.geoportal.config.ThreadTokens;
import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.domain.GeoUser;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.List;
import java.util.Map;

public class BasicAuthenticator {

    GeoUserDao geoUserDao;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public BasicAuthenticator(GeoUserDao geoUserDao) {
        this.geoUserDao = geoUserDao;
    }

    public void doAuthentication(WebServiceContext webServiceContext)
    {
        MessageContext msgContext = webServiceContext.getMessageContext();
        Map http_headers = (Map) msgContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        List<String> authHeaders = (List) http_headers.get("Authorization");

        if (authHeaders == null || authHeaders.isEmpty())
            throw new RuntimeException("Need basic authentication");

        String authHeader = authHeaders.get(0);

        try {
            String userPsw = new String(Base64.decode((authHeader).replace("Basic ","")));
            String creds[] = userPsw.split(":");
            String username = creds[0], password = creds[1];

            GeoUser geoUser = geoUserDao.get(username);

            if (geoUser == null || !encoder.matches(password, geoUser.getPassword()))
                throw new RuntimeException("Wrong credentials");

            ThreadTokens.INSTANCE.put(Thread.currentThread().getId(), geoUser.getToken());
        } catch (WSSecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
