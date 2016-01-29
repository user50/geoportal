package org.w2fc.geoportal.ws.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.geometry.GeometryParameter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/object/RequestPoint")
public class RequestPoint implements Serializable, GeometryParameter{
	private static final long serialVersionUID = -3250784736508978802L;

	@XmlElement(name = "name", required=true)
	private String name;

	@XmlElement(name = "point", required = true)
	private PointCoordinates pointCoordinates;

	@XmlElement(name = "layerId", required=true)
	private Long layerId;

	@XmlElement(name = "address")
	private String address;

	@XmlElementWrapper(name="tags")
    @XmlElement(name="tag")
	private Set<GeoObjectTag> tags;

	@XmlElement(name = "wkt")
	private String wkt;

	public RequestPoint() {
	}

	public RequestPoint(String name, Long layerId, PointCoordinates pointCoordinates) {
		this.name = name;
		this.layerId = layerId;
		this.pointCoordinates = pointCoordinates;
	}

	public RequestPoint(String name, Long layerId, String address) {
		this.name = name;
		this.layerId = layerId;
		this.address = address;
	}

	@Override
	public String getWkt() {
		return wkt;
	}

	public void setWkt(String wkt) {
		this.wkt = wkt;
	}

	public PointCoordinates getPointCoordinates() {
		return pointCoordinates;
	}

	public void setPointCoordinates(PointCoordinates pointCoordinates) {
		this.pointCoordinates = pointCoordinates;
	}

	public Long getLayerId() {
		return layerId;
	}
	public void setLayerId(Long layerId) {
		this.layerId = layerId;
	}
	public Set<GeoObjectTag> getTags() {
		return tags;
	}
	public void setTags(Set<GeoObjectTag> tags) {
		this.tags = tags;
	}

	@Override
	public GeoObjectGeometryType getType() {
		return GeoObjectGeometryType.POINT;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
