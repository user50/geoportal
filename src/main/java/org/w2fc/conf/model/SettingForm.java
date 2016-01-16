package org.w2fc.conf.model;

import java.io.Serializable;
import java.util.List;

public class SettingForm implements Serializable {
	private List<SettingsModel> settings;

	
	public List<SettingsModel> getSettings() {
		return settings;
	}

	public void setSettings(List<SettingsModel> settings) {
		this.settings = settings;
	}
}
