package org.w2fc.geoportal.dao;

import java.util.Set;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.GeoACL;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.domain.GeoUserRole;


@Repository
@Service
public class GeoUserRoleDaoImpl extends AbstractDaoDefaulImpl<GeoUserRole, Long> implements GeoUserRoleDao {

    protected GeoUserRoleDaoImpl() {
        super(GeoUserRole.class);
    }
    
    
    @Override
    public void remove(Long identifier, boolean... forceFlush) {
        Session s = getCurrentSession();
        GeoUserRole r = get(identifier);
        
        Set<GeoACL> geoACLs = r.getGeoACLs();
        for (GeoACL geoACL : geoACLs) {
            geoACL.getGeoUserRoles().remove(r);
        }
        geoACLs.clear();
        
        Set<GeoUser> geoUsers = r.getGeoUsers();
        for (GeoUser geoUser : geoUsers) {
            geoUser.getGeoUserRoles().remove(r);
        }
        geoUsers.clear();
        
        // orphanRemoval
        r.getLayerToRoleReferences().clear();
        
        s.delete(r);
        flushSession(s, forceFlush);
    }
}
