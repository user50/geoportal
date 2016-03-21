package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.domain.GeoObjectTag;

import java.util.List;
import java.util.Set;

public class MultiPolygon implements GeometryParameter {

    private String name;
    private String guid;
    private String extSysId;
    private GeoObjectGeometryType type;
    private List<GeoPolygon> polygons;
    private Long layerId;
    private Set<GeoObjectTag> tags;
    private String wkt;
    private String spatialKey;

    public MultiPolygon(String name, Long layerId, List<GeoPolygon> polygons) {
        this.name = name;
        this.layerId = layerId;
        this.polygons = polygons;
    }

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

    @Override
    public GeoObjectGeometryType getType() {
        return GeoObjectGeometryType.MULTIPOLYGON;
    }

    public void setType(GeoObjectGeometryType type) {
        this.type = type;
    }

    public List<GeoPolygon> getPolygons() {
        return polygons;
    }

    public void setPolygons(List<GeoPolygon> polygons) {
        this.polygons = polygons;
    }

    @Override
    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
    }

    @Override
    public Set<GeoObjectTag> getTags() {
        return tags;
    }

    public void setTags(Set<GeoObjectTag> tags) {
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
    public String getSpatialKey() {
        return spatialKey;
    }

    public void setSpatialKey(String spatialKey) {
        this.spatialKey = spatialKey;
    }
}
