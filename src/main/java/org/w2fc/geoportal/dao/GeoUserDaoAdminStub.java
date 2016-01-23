package org.w2fc.geoportal.dao;

import org.w2fc.geoportal.domain.GeoUser;

public class GeoUserDaoAdminStub extends GeoUserDaoWrapper {
    public GeoUserDaoAdminStub(GeoUserDao geoUserDao) {
        super(geoUserDao);
    }

    @Override
    public GeoUser getCurrentGeoUser() {
        return get(1l);
    }
}
