package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.domain.GeoObjectTag;

import java.util.List;
import java.util.Set;

/**
 * @author Yevhen
 */
public class MultiLineString implements GeometryParameter {

    private String name;
    private String guid;
    private String extSysId;
    private GeoObjectGeometryType type;
    private List<GeoLineString> lineStrings;
    private Set<Long> layerIds;
    private Set<GeoObjectTag> tags;
    private String wkt;
    private String spatialKey;

    public MultiLineString(String name, Set<Long> layerIds, List<GeoLineString> lineStrings) {
        this.name = name;
        this.layerIds = layerIds;
        this.lineStrings = lineStrings;
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
    public Set<Long> getLayerIds() {
        return layerIds;
    }

    public void setLayerIds(Set<Long> layerIds) {
        this.layerIds = layerIds;
    }

    public List<GeoLineString> getLineStrings() {
        return lineStrings;
    }

    public void setLineStrings(List<GeoLineString> lineStrings) {
        this.lineStrings = lineStrings;
    }

    @Override
    public String getWkt() {
        return wkt;
    }

    @Override
    public String getSpatialKey() {
        return spatialKey;
    }

    public void setSpatialKey(String spatialKey) {
        this.spatialKey = spatialKey;
    }

    public void setWkt(String wkt) {
        this.wkt = wkt;
    }

    @Override
    public Set<GeoObjectTag> getTags() {
        return tags;
    }

    public void setTags(Set<GeoObjectTag> tags) {
        this.tags = tags;
    }

    @Override
    public GeoObjectGeometryType getType() {
        return GeoObjectGeometryType.MULTIPOINT;
    }
}
