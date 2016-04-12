package org.w2fc.geoportal.dao;

import java.util.List;

import org.w2fc.conf.model.SettingsModel;
import org.w2fc.geoportal.domain.GeoSettings;

public interface GeoSettingsDao extends AbstractDao<GeoSettings, Long>{

	GeoSettings getByName(String string);
	
	void updateSettings(final List<SettingsModel> settings);

}
