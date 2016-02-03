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
public class RequestPolygon implements Serializable, GeometryParameter{

    private static final long serialVersionUID = -3250784736508978802L;

    @XmlElement(name = "name", required=true)
    private String name;

    private String guid;

    @XmlElementWrapper(name = "points", required = true)
    private PointCoordinates[] pointsCoordinates;

    @XmlElementWrapper(name = "polygonHoles", required = true)
    private LineCoordinates[] polygonHoles;

    @XmlElement(name = "layerId", required=true)
    private Long layerId;

    @XmlElementWrapper(name="tags")
    @XmlElement(name="tag")
    private Set<GeoObjectTag> tags;

    @XmlElement(name = "wkt", required=false)
    private String wkt;

    @XmlElement(name="spatialKey")
    private String spatialKey;

    public RequestPolygon() {
    }

    public RequestPolygon(String name, Long layerId, PointCoordinates[] pointsCoordinates) {
        this.name = name;
        this.layerId = layerId;
        this.pointsCoordinates = pointsCoordinates;
    }

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

    public LineCoordinates[] getPolygonHoles() {
        return polygonHoles;
    }

    public void setPolygonHoles(LineCoordinates[] polygonHoles) {
        this.polygonHoles = polygonHoles;
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
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public GeoObjectGeometryType getType() {
        return GeoObjectGeometryType.POLYGON;
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
