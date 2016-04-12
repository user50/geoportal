package org.w2fc.geoportal.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.hibernatespatial.GeometryUserType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w2fc.conf.Constants;
import org.w2fc.geoportal.domain.GeoACL;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.domain.GeoUserRole;
import org.w2fc.geoportal.user.CustomUserDetails;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;


@Repository
@Service
public class GeoUserDaoImpl extends AbstractDaoDefaulImpl<GeoUser, Long> implements GeoUserDao {

    protected GeoUserDaoImpl() {
        super(GeoUser.class);
    }

	@Override
    public GeoUser getCurrentGeoUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof AnonymousAuthenticationToken){
            return get(Constants.GEOPORTAL_USER_ANONYMOUS_ID);
        }

        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
        return get(ud.getId());
    }
    
    
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    @Override
    public List<GeoUser> list() {
        return getCurrentSession().createQuery("from GeoUser where enabled=true or id=0 order by login").list();
    }
    
    
    @Override
    public void remove(Long identifier, boolean... forceFlush) {
        Session s = getCurrentSession();
        GeoUser u = get(identifier);
        
        Set<GeoACL> geoACLs = u.getGeoACLs();
        for (GeoACL geoACL : geoACLs) {
            geoACL.getGeoUsers().remove(u);
        }
        geoACLs.clear();
        
        Set<GeoUserRole> geoRoles = u.getGeoUserRoles();
        for (GeoUserRole role : geoRoles) {
            role.getGeoUsers().remove(u);
        }
        geoRoles.clear();

        // orphanRemoval
        u.getLayerToUserReferences().clear();
        
        s.delete(u);
        flushSession(s, forceFlush);
    }

    private final static GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Geometry> getPermissionArea(Long id) {
		String sql = getResourceSQL("GeoUserDao.getPermissionArea");
		List<Object[]> rows = getCurrentSession().createSQLQuery(sql).
				addScalar("path",  StandardBasicTypes.INTEGER).
				addScalar("geom",  GeometryUserType.TYPE).
				setLong("id", id).
	            list();
		if (rows != null) {
			Coordinate[] bounds = new Coordinate[]{new Coordinate(0d,0d), new Coordinate(80d,0d), new Coordinate(80d,360d), new Coordinate(0d,360d), new Coordinate(0d,0d)};
			LinearRing shell = factory.createLinearRing(bounds);
			List<LinearRing> holesArray = new ArrayList<LinearRing>();
			Geometry permissionArea = null;
			List<Polygon> islands = new ArrayList<Polygon>();
			for(Object[] row : rows){
				Integer path = (Integer) row[0];
				Geometry area = (Geometry) row[1];
				if(path == -1){
					permissionArea = area;
					continue;
				}
				if(path != 0){
					islands.add((Polygon) area);
					continue;
				}
				holesArray.add(factory.createLinearRing(area.getCoordinates()));
			}
			LinearRing[] holes  = holesArray.toArray(new LinearRing[holesArray.size()]);
			
			Polygon polygon = factory.createPolygon(shell, holes  );
			
			Map<String, Geometry> result = new HashMap<String, Geometry>();
			if(permissionArea == null)return null;
			result.put("shell", polygon);
			result.put("area", permissionArea);
			
			if(islands.size() > 0){
				//islands.add(polygon);
				GeometryCollection multi = factory.createGeometryCollection(islands.toArray(new Polygon[islands.size()]));
				result.put("islands", multi);
			}
			
			return result;
		}
		return null;
	}


	@Override
	public GeoUser get(String login) {
		return (GeoUser) getCurrentSession().createQuery("from GeoUser where login=:login").setString("login", login).uniqueResult();
	}

	@Override
	public GeoUser get(Long id) {
		return (GeoUser) getCurrentSession().createQuery("from GeoUser where id=:id").setLong("id", id).uniqueResult();
	}

	@Override
	public GeoUser getByToken(String token) {
		return (GeoUser) getCurrentSession().createQuery("from GeoUser where enabled=true and token=:token")
				.setString("token", token).uniqueResult();
	}
}
