package org.w2fc.geoportal.ws.geometry;

import org.junit.Test;
import org.w2fc.geoportal.ws.model.PolygonHole;

import java.util.List;

import static org.junit.Assert.*;

public class PolygonHoleFromJsonFactoryTest {

    @Test
    public void testCreate() throws Exception {
        String json = "[[[57.4668, 37.4568], [57.65489, 37.23456] , [57.2222, 37.1111], [57.4668, 37.4568]],[[57.4668, 37.4568], [57.65489, 37.23456] , [57.2222, 37.1111], [57.4668, 37.4568]]]";

        List<PolygonHole> holes = new PolygonHoleFromJsonFactory().create(json);

        assertEquals(2, holes.size());
    }
}