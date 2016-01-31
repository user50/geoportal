package org.w2fc.geoportal.ws.geometry;

import com.vividsolutions.jts.geom.Geometry;
import org.w2fc.geoportal.ws.geometry.builder.WKTGeometryBuilder;
import org.w2fc.geoportal.ws.model.RequestPoint;

public class WKTGeometryBuilderTest {

    @org.junit.Test
    public void testCreate() throws Exception {
        String wkt = "POLYGON ((39.8838 57.6116, 39.8748 57.6616, 39.8658 57.6416, 39.8838 57.6116), (39.8838 60.6116, 39.87475 57.6616, 39.8658 57.6415, 39.8838 60.6116))";

        RequestPoint geometryParameter = new RequestPoint();
        geometryParameter.setWkt(wkt);

        WKTGeometryBuilder builder = new WKTGeometryBuilder();
        Geometry geometry = builder.create(geometryParameter);
    }
}