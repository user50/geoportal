package org.w2fc.geoportal.ws.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.geometry.GeometryParameter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/object/RequestPoint")
public class RequestPoint implements Serializable, GeometryParameter{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3250784736508978802L;
	
	@XmlElement(name = "name", required=true)
	private String name;
	@XmlElement(name = "lat", required=true)
	private Double lat;
	@XmlElement(name = "lon", required=true)
	private Double lon;
	@XmlElement(name = "timetick", required=false, defaultValue="01.01.1970 12:00:00")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date timetick;
	@XmlElement(name = "calibrate", required=false, defaultValue="false")
	private Boolean calibrate;
	@XmlElement(name = "layerId", required=true)
	private Long layerId;
	
	@XmlElementWrapper(name="tags")
    @XmlElement(name="tag")
    HashSet<GeoObjectTag> tags;
    
	
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
	public Date getTimetick() {
		return timetick;
	}
	public void setTimetick(Date timetick) {
		this.timetick = timetick;
	}
	public Boolean getCalibrate() {
		return calibrate;
	}
	public void setCalibrate(Boolean calibrate) {
		this.calibrate = calibrate;
	}
	public Long getLayerId() {
		return layerId;
	}
	public void setLayerId(Long layerId) {
		this.layerId = layerId;
	}
	public HashSet<GeoObjectTag> getTags() {
		return tags;
	}
	public void setTags(HashSet<GeoObjectTag> tags) {
		this.tags = tags;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
