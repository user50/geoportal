package org.w2fc.geoportal.dao;


import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w2fc.geoportal.domain.GeoACL;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.domain.GeoUserRole;


@Service
@Repository
public class GeoACLDaoImpl extends AbstractDaoDefaulImpl<GeoACL, Long> implements GeoACLDao{

    final Logger logger = LoggerFactory.getLogger(GeoACLDaoImpl.class);
    
    protected GeoACLDaoImpl() {
        super(GeoACL.class);
    }
    
    
    @Override
    public void remove(Long identifier, boolean... forceFlush) {
        Session s = getCurrentSession();
        GeoACL acl = get(identifier);
        
        Set<GeoUserRole> role = acl.getGeoUserRoles();
        for (GeoUserRole geoACL : role) {
            geoACL.getGeoACLs().remove(acl);
        }
        role.clear();
        
        Set<GeoUser> geoUsers = acl.getGeoUsers();
        for (GeoUser geoUser : geoUsers) {
            geoUser.getGeoACLs().remove(acl);
        }
        geoUsers.clear();
        
        s.delete(acl);
        flushSession(s, forceFlush);
    }


	@Override
	public GeoACL getByObjectId(Long id) {
		return (GeoACL) getCurrentSession()
                .createQuery("Select acl from GeoACL acl where acl.geoObject.id = :id")
                .setLong("id", id)
                .uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> listAllUsedObjects(){
		return getCurrentSession().createQuery("Select acl.geoObject from GeoACL acl order by acl.name").list();
	}

	@Override
	public List<GeoObject> listByUser(Long userId) {
		return getCurrentSession()
				.createQuery("Select acl.geoObject from GeoACL acl join acl.geoUsers u where u.id = :userId")
				.setParameter("userId", userId).list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<GeoACL> list() {
		return getCurrentSession().createQuery("from GeoACL order by name").list();

	}
}
