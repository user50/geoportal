package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.domain.GeoObjectTag;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.HashSet;

/**
 * @author yevhenlozov
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/object/RequestGeoObject")
public class RequestGeoObject implements Serializable {

    @XmlElement(name = "id")
    private Long id;

    @XmlElement(name = "guid")
    private Long guid;

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

    @XmlElement(name = "address")
    private String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuid() {
        return guid;
    }

    public void setGuid(Long guid) {
        this.guid = guid;
    }

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

    public String getWkt() {
        return wkt;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    public GeoObjectGeometryType getType() {
        return type;
    }

    public void setType(GeoObjectGeometryType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
