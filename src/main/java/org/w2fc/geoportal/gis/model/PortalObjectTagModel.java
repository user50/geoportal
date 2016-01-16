package org.w2fc.geoportal.gis.model;

import java.io.Serializable;

public class PortalObjectTagModel implements Serializable{

	String key;
	String value;
	Long id;
	
	public PortalObjectTagModel() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
