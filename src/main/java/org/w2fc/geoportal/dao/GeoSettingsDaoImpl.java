package org.w2fc.geoportal.dao;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.GeoSettings;

@Repository
@Service
public class GeoSettingsDaoImpl extends AbstractDaoDefaulImpl<GeoSettings, Long> implements GeoSettingsDao  {

	protected GeoSettingsDaoImpl() {
		super(GeoSettings.class);
		
	}

	

}
