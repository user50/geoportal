package org.w2fc.geoportal.dao;

import org.hibernate.SessionFactory;
import org.w2fc.geoportal.config.ThreadTokens;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.ws.exception.AccessDeniedException;
import org.w2fc.geoportal.ws.exception.GeoPortalException;

public class TokenizedGeoUserDao extends GeoUserDaoWrapper {

    private SessionFactory sessionFactory;

    public TokenizedGeoUserDao(GeoUserDao geoUserDao, SessionFactory sessionFactory) {
        super(geoUserDao);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public GeoUser getCurrentGeoUser() {
        String token = getToken();

        if (token == null)
            return super.getCurrentGeoUser();

        GeoUser geoUser = super.getByToken(token);

        if (geoUser == null)
            throw new AccessDeniedException("Token is invalid");

        return geoUser;
    }

    private String getToken(){
        long threadId = Thread.currentThread().getId();

        return ThreadTokens.INSTANCE.getToken(threadId);
    }
}
