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
public class RequestLine implements Serializable, GeometryParameter{

    private static final long serialVersionUID = -3250784736508978802L;

    public RequestLine() {
    }

    public RequestLine(String name, Set<Long> layerIds, PointCoordinates[] pointsCoordinates) {
        this.name = name;
        this.layerIds = layerIds;
        this.pointsCoordinates = pointsCoordinates;
    }

    @XmlElement(name = "name", required=true)
    private String name;

    private String guid;

    private String extSysId;

    @XmlElementWrapper(name = "points", required = true)
    @XmlElement(name = "point")
    private PointCoordinates[] pointsCoordinates;

    private Set<Long> layerIds;

    @XmlElementWrapper(name="tags")
    @XmlElement(name="tag")
    private Set<GeoObjectTag> tags;

    @XmlElement(name = "wkt")
    private String wkt;

    @XmlElement(name = "spatialKey")
    private String spatialKey;

    @Override
    public String getWkt() {
        return wkt;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    public PointCoordinates[] getPointsCoordinates() {
        return pointsCoordinates;
    }

    public void setPointsCoordinates(PointCoordinates[] pointsCoordinates) {
        this.pointsCoordinates = pointsCoordinates;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public String getExtSysId() {
        return extSysId;
    }

    public void setExtSysId(String extSysId) {
        this.extSysId = extSysId;
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
        return GeoObjectGeometryType.LINESTRING;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSpatialKey() {
        return spatialKey;
    }

    public void setSpatialKey(String spatialKey) {
        this.spatialKey = spatialKey;
    }
}
