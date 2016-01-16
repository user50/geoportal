package org.w2fc.geoportal.gis.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortalObjectModel implements Serializable{
	
	private Long id;
	private String name;
	public List<PortalObjectTagModel> properties;
	public Double lat;
	public Double lon;
	public String wkt;
	public Integer lineWeight;
	public String fillColor;
	public String lineColor;
	public String fiasCode;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PortalObjectTagModel> getProperties() {
		return properties;
	}
	
	public void setProperties(List<PortalObjectTagModel> properties) {
		this.properties = properties;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public String getWkt() {
		return wkt;
	}
	public void setWkt(String wkt) {
		this.wkt = wkt;
	}
    public void setLineWeight(Integer lineWeight) {
        this.lineWeight = lineWeight;
    }
    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }
    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }
    public Integer getLineWeight() {
        return lineWeight;
    }
    public String getFillColor() {
        return fillColor;
    }
    public String getLineColor() {
        return lineColor;
    }
	public String getFiasCode() {
		return fiasCode;
	}
	public void setFiasCode(String fiasCode) {
		this.fiasCode = fiasCode;
	}
	
}