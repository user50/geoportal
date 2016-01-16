package org.w2fc.geoportal.dao;

import java.util.List;

import org.w2fc.geoportal.domain.GeoACL;
import org.w2fc.geoportal.domain.GeoObject;


public interface GeoACLDao extends AbstractDao<GeoACL, Long>{

	GeoACL getByObjectId(Long id);

	List<GeoObject> listAllUsedObjects();
}
