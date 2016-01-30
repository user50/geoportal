package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.geometry.GeometryParameter;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.HashSet;

/**
 * @author yevhenlozov
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/object/RequestGeoObject")
public class RequestGeoObject implements Serializable, GeometryParameter {

    @XmlElement(name = "name", required=true)
    private String name;

    @XmlElement(name = "type")
    private GeoObjectGeometryType type;

    @XmlElement(name = "layerId", required=true)
    private Long layerId;

    @XmlElement(name = "wkt")
    private String wkt;

    @XmlElementWrapper(name="tags")
    @XmlElement(name="tag")
    private HashSet<GeoObjectTag> tags;

    @XmlElement(name = "points", required = true)
    private String pointsCoordinates; // json array of arrays: [[lat,lon], [lat,lon]]

    @XmlElement(name = "polygonHoles") // json array of arrays of arrays: [[lat,lon], [lat,lon]]
    private String polygonHoles;

    @XmlElement(name = "address")
    private String address;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPointsCoordinates() {
        return pointsCoordinates;
    }

    public void setPointsCoordinates(String pointsCoordinates) {
        this.pointsCoordinates = pointsCoordinates;
    }

    @Override
    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
    }

    @Override
    public HashSet<GeoObjectTag> getTags() {
        return tags;
    }

    public void setTags(HashSet<GeoObjectTag> tags) {
        this.tags = tags;
    }

    @Override
    public String getWkt() {
        return wkt;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    @Override
    public GeoObjectGeometryType getType() {
        return type;
    }

    public void setType(GeoObjectGeometryType type) {
        this.type = type;
    }

    public String getPolygonHoles() {
        return polygonHoles;
    }

    public void setPolygonHoles(String polygonHoles) {
        this.polygonHoles = polygonHoles;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
