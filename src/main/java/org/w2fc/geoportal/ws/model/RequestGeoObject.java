package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.domain.GeoObjectTag;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yevhenlozov
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/object/RequestGeoObject")
public class RequestGeoObject implements Serializable, GeometryParameter {

    @XmlElement(name = "name", required=true)
    private String name;

    @XmlElement(name = "guid")
    private String guid;

    @XmlElement(name = "extSysId")
    private String extSysId;

    @XmlElement(name = "type")
    private GeoObjectGeometryType type;

    @XmlElementWrapper(name="layers", required = true)
    @XmlElement(name="layerId")
    private Set<Long> layerIds;

    @XmlElement(name = "wkt")
    private String wkt;

    @XmlElementWrapper(name="tags")
    @XmlElement(name="tag")
    private HashSet<GeoObjectTag> tags;

    @XmlElement(name = "points", required = true)
    private String pointsCoordinates; // json array of arrays: [[lat,lon], [lat,lon]]

    @XmlElement(name = "address")
    private String address;

    @XmlElement(name = "spatialKey")
    private String spatialKey;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPointsCoordinates() {
        return pointsCoordinates;
    }

    public void setPointsCoordinates(String pointsCoordinates) {
        this.pointsCoordinates = pointsCoordinates;
    }

    public Set<Long> getLayerIds() {
        return layerIds;
    }

    public void setLayersIds(Set<Long> layerIds) {
        this.layerIds = layerIds;
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
