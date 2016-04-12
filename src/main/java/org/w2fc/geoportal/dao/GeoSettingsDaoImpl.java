package org.w2fc.geoportal.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w2fc.conf.model.SettingsModel;
import org.w2fc.geoportal.domain.GeoSettings;

@Repository
@Service
public class GeoSettingsDaoImpl extends AbstractDaoDefaulImpl<GeoSettings, Long> implements GeoSettingsDao  {

	protected GeoSettingsDaoImpl() {
		super(GeoSettings.class);
		
	}

	@Transactional(readOnly = true)
	@Override
	public GeoSettings getByName(final String name) {
		
		return (GeoSettings)getCurrentSession()
					.getNamedQuery(GeoSettings.Query.BY_NAME)
					.setString(0, name)
					.uniqueResult();
	}

	@Transactional
	@Override
	public void updateSettings(final List<SettingsModel> settings) {
		for(final SettingsModel set : settings){
			GeoSettings setting = get(set.getId());
			setting.setValue(set.getValue());
			update(setting, true);
		}
	}

}
