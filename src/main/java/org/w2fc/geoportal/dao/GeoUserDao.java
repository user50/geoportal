package org.w2fc.geoportal.dao;

import java.util.Map;

import org.w2fc.geoportal.domain.GeoUser;

import com.vividsolutions.jts.geom.Geometry;


public interface GeoUserDao extends AbstractDao<GeoUser, Long> {
    
    public GeoUser getCurrentGeoUser();

	public Map<String, Geometry> getPermissionArea(Long id);

	public GeoUser get(String login);

	public GeoUser getByToken(String token);

}
