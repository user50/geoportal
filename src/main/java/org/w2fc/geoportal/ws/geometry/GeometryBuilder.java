package org.w2fc.geoportal.ws.geometry;


import com.vividsolutions.jts.geom.Geometry;

public interface GeometryBuilder<T extends GeometryParameter> {

    Geometry create(T parameters);

}
