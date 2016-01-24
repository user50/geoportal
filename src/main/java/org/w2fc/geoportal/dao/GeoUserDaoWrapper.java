package org.w2fc.geoportal.dao;

import com.vividsolutions.jts.geom.Geometry;
import org.w2fc.geoportal.domain.GeoUser;

import java.util.List;
import java.util.Map;

public class GeoUserDaoWrapper implements GeoUserDao {

    GeoUserDao geoUserDao;

    public GeoUserDaoWrapper(GeoUserDao geoUserDao) {
        this.geoUserDao = geoUserDao;
    }

    @Override
    public GeoUser getCurrentGeoUser() {
        return geoUserDao.getCurrentGeoUser();
    }

    @Override
    public Map<String, Geometry> getPermissionArea(Long id) {
        return geoUserDao.getPermissionArea(id);
    }

    @Override
    public GeoUser get(String login) {
        return geoUserDao.get(login);
    }

    @Override
    public GeoUser getByToken(String token) {
        return geoUserDao.getByToken(token);
    }

    @Override
    public GeoUser add(GeoUser object, boolean... forceFlush) {
        return geoUserDao.add(object, forceFlush);
    }

    @Override
    public List<GeoUser> list() {
        return geoUserDao.list();
    }

    @Override
    public GeoUser get(Long identifier) {
        return geoUserDao.get(identifier);
    }

    @Override
    public GeoUser get(GeoUser object) {
        return geoUserDao.get(object);
    }

    @Override
    public void remove(Long identifier, boolean... forceFlush) {
        geoUserDao.remove(identifier, forceFlush);
    }

    @Override
    public GeoUser update(GeoUser object, boolean... forceFlush) {
        return geoUserDao.update(object, forceFlush);
    }

    @Override
    public String getResourceSQL(String name) {
        return geoUserDao.getResourceSQL(name);
    }
}
