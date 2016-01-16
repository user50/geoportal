package org.w2fc.conf.model;

import java.io.Serializable;

public class SettingsModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7467159549637487892L;
	public Long id;
	public String key;
	public String value;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
