package org.w2fc.geoportal.gis;

import java.util.List;
import java.util.Map;

import org.w2fc.geoportal.domain.GeoObject;

public class GeoObjectUIAdapterLite extends GeoObjectUI {

	public GeoObjectUIAdapterLite(GeoObject obj) {
		super(obj);
	}

	@Override
	public String getCreator() {
		return null;
	}

	
	@Override
	public String getEditor() {
		return null;
	}

	@Override
	public List<Map<String, String>> getTags() {
		return null;
	}

	@Override
	public String getLineColor() {
		return null;
	}

	@Override
	public Boolean getIcon() {
		return false;
	}

	@Override
	public String getFillColor() {
		return null;
	}

	@Override
	public Integer getLineWeight() {
		return null;
	}	
}
