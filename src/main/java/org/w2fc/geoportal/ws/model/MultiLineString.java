package org.w2fc.geoportal.ws.model;

import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.ws.geometry.GeometryParameter;

import java.util.List;
import java.util.Set;

/**
 * @author Yevhen
 */
public class MultiLineString implements GeometryParameter {

    private String name;
    private GeoObjectGeometryType type;
    private List<GeoLineString> lineStrings;
    private Long layerId;
    private Set<GeoObjectTag> tags;
    private String wkt;

    public MultiLineString(String name, Long layerId, List<GeoLineString> lineStrings) {
        this.name = name;
        this.layerId = layerId;
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
    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
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
