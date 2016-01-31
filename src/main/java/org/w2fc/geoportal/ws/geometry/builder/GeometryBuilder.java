package org.w2fc.geoportal.ws.geometry.builder;


import com.vividsolutions.jts.geom.Geometry;
import org.w2fc.geoportal.ws.model.GeometryParameter;

public interface GeometryBuilder<T extends GeometryParameter> {

    Geometry create(T parameters);

}
