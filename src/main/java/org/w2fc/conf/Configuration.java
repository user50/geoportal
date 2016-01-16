package org.w2fc.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.w2fc.conf.model.SettingsModel;

public class Configuration extends PropertyPlaceholderConfigurer {

	private static Map<String, String> propertiesMap;

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactory, Properties props)
			throws BeansException {
		super.processProperties(beanFactory, props);

		propertiesMap = new HashMap<String, String>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String valueStr = resolvePlaceholder(keyStr, props);
			propertiesMap.put(keyStr, valueStr);
		}
	}

	public static String getProperty(String name) {
		return propertiesMap.get(name).toString();
	}

	public static List<SettingsModel> getPropertyList() {
		List<SettingsModel> res = new ArrayList<SettingsModel>();
		for (String key : propertiesMap.keySet()) {
			SettingsModel setting = new SettingsModel();
			setting.setKey(key);
			setting.setValue(propertiesMap.get(key));
			res.add(setting);
		}
		return res;
	}
}
