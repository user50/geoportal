package org.w2fc.geoportal.ws.model;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.w2fc.geoportal.domain.GeoObjectTag;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/object/RequestPoint")
public class RequestPoint implements Serializable, GeometryParameter{
	private static final long serialVersionUID = -3250784736508978802L;

	@XmlElement(name = "name", required=true)
	private String name;

	private String guid;

	private String extSysId;

	@XmlElement(name = "point", required = true)
	private PointCoordinates pointCoordinates;

	private Set<Long> layerIds;

	@XmlElement(name = "address")
	private String address;

	@XmlElementWrapper(name="tags")
    @XmlElement(name="tag")
	private Set<GeoObjectTag> tags;

	@XmlElement(name = "wkt")
	private String wkt;

	@XmlElement(name = "spatialKey")
	private String spatialKey;

	public RequestPoint() {
	}

	public RequestPoint(String name, Set<Long> layerIds, PointCoordinates pointCoordinates) {
		this.name = name;
		this.layerIds = layerIds;
		this.pointCoordinates = pointCoordinates;
	}

	public RequestPoint(String name, Set<Long> layerIds, String address) {
		this.name = name;
		this.layerIds = layerIds;
		this.address = address;
	}

	@Override
	public String getWkt() {
		return wkt;
	}

	public void setWkt(String wkt) {
		this.wkt = wkt;
	}

	@Override
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getExtSysId() {
		return extSysId;
	}

	public void setExtSysId(String extSysId) {
		this.extSysId = extSysId;
	}

	public PointCoordinates getPointCoordinates() {
		return pointCoordinates;
	}

	public void setPointCoordinates(PointCoordinates pointCoordinates) {
		this.pointCoordinates = pointCoordinates;
	}

	@Override
	public Set<Long> getLayerIds() {
		return layerIds;
	}

	public void setLayerIds(Set<Long> layerIds) {
		this.layerIds = layerIds;
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

	@Override
	public String getSpatialKey() {
		return spatialKey;
	}

	public void setSpatialKey(String spatialKey) {
		this.spatialKey = spatialKey;
	}
}
