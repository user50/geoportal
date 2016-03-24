package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.domain.GeoObjectTag;

import java.util.Set;

/**
 * @author Yevhen
 */
public class MultiPoint implements GeometryParameter {

    private String name;
    private String guid;
    private String extSysId;
    private GeoObjectGeometryType type;
    private PointCoordinates[] pointsCoordinates;
    private Set<Long> layerIds;
    private Set<GeoObjectTag> tags;
    private String wkt;
    private String spatialKey;

    public MultiPoint(String name, Set<Long> layerIds, PointCoordinates[] pointsCoordinates) {
        this.name = name;
        this.layerIds = layerIds;
        this.pointsCoordinates = pointsCoordinates;
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

    public PointCoordinates[] getPointsCoordinates() {
        return pointsCoordinates;
    }

    public void setPointsCoordinates(PointCoordinates[] pointsCoordinates) {
        this.pointsCoordinates = pointsCoordinates;
    }

    @Override
    public String getWkt() {
        return wkt;
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

    @Override
    public String getSpatialKey() {
        return spatialKey;
    }

    public void setSpatialKey(String spatialKey) {
        this.spatialKey = spatialKey;
    }
}
