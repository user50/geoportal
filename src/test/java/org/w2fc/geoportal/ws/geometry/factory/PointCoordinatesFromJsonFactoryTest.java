package org.w2fc.geoportal.ws.geometry.factory;

import org.junit.Test;
import org.w2fc.geoportal.ws.geometry.factory.PointCoordinatesFromJsonFactory;
import org.w2fc.geoportal.ws.model.PointCoordinates;

import java.util.List;

import static org.junit.Assert.*;

public class PointCoordinatesFromJsonFactoryTest {

    @Test
    public void testCreate() throws Exception {
        String json = "[[57.4668, 37.4568], [57.65489, 37.23456] , [57.2222, 37.1111], [57.4668, 37.4568]]";

        List<PointCoordinates> pointsCoordinates = new PointCoordinatesFromJsonFactory().create(json);

        assertEquals(4, pointsCoordinates.size());
    }
}