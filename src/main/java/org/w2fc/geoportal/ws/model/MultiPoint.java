package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.geometry.GeometryParameter;

import java.util.Set;

/**
 * @author Yevhen
 */
public class MultiPoint implements GeometryParameter {

    private String name;
    private GeoObjectGeometryType type;
    private PointCoordinates[] pointsCoordinates;
    private Long layerId;
    private Set<GeoObjectTag> tags;
    private String wkt;

    public MultiPoint(String name, Long layerId, PointCoordinates[] pointsCoordinates) {
        this.name = name;
        this.layerId = layerId;
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
    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
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
}