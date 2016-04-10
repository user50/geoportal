package org.w2fc.conf;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w2fc.conf.model.SettingForm;
import org.w2fc.conf.model.SettingsModel;
import org.w2fc.geoportal.dao.GeoSettingsDao;
import org.w2fc.geoportal.domain.GeoSettings;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.spring.AbstractController;

import javax.annotation.PostConstruct;

@Controller
@RequestMapping(value = "/settings")
public class SettingsController extends AbstractController<GeoSettings, GeoSettingsDao, Long>{

	@Autowired
	ServiceRegistry serviceRegistry;

	@RequestMapping(value = "/")
    @Transactional
	@PreAuthorize("@geoportalSecurity.isAdministrator()")
    public String getSettings(@ModelAttribute SettingForm settings, Model model) {
       
		if(settings.getSettings() != null && settings.getSettings().size() > 0){
			updateSettings(settings.getSettings());
		}
		
		model.addAttribute("settings", getDao().list());
		model.addAttribute("params", Configuration.getPropertyList());
		
		return "markup/settings";
    }

	private void updateSettings(List<SettingsModel> settings) {
		for(SettingsModel set : settings){
			GeoSettings setting = getDao().get(set.getId());
			setting.setValue(set.getValue());
			getDao().update(setting, true);
		}
		
	}

	@Override
	@Autowired
	public void setAutowiredDao(GeoSettingsDao dao) {
		setDao(dao);
	}

	@PostConstruct
	public void init() {
		setAutowiredDao(serviceRegistry.getGeoSettingsDao());
	}
}
