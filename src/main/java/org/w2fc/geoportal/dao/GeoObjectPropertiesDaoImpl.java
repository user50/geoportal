package org.w2fc.geoportal.dao;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.GeoObjectProperties;

@Service
@Repository
public class GeoObjectPropertiesDaoImpl extends AbstractDaoDefaulImpl<GeoObjectProperties, Long> implements GeoObjectPropertiesDao {

	protected GeoObjectPropertiesDaoImpl() {
		super(GeoObjectProperties.class);
	}	
}
