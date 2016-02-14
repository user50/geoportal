package org.w2fc.geoportal.ws.geometry.builder;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w2fc.geoportal.dao.ReferenceSystemProjDao;
import org.w2fc.geoportal.ws.model.GeometryParameter;

public class TransformCoordinate<T extends GeometryParameter> implements GeometryBuilder<T> {

    final static Logger logger = LoggerFactory.getLogger(TransformCoordinate.class);

    public static final String DEFAULT_SPATIAL_REF_SYS = "WGS84";

    GeometryBuilder<T> geometryBuilder;
    ReferenceSystemProjDao referenceSystemDao;
    CoordinateReferenceSystem targetSystem;

    public TransformCoordinate(GeometryBuilder<T> geometryBuilder, ReferenceSystemProjDao referenceSystemDao) {
        this.referenceSystemDao = referenceSystemDao;
        this.geometryBuilder = geometryBuilder;

        try {
            targetSystem = CRS.decode("EPSG:4326");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            targetSystem = null;
        }
    }

    @Override
    public Geometry create(T parameters) {

        Geometry geometry = geometryBuilder.create(parameters);

        if(parameters.getSpatialKey() == null || parameters.getSpatialKey().equals(DEFAULT_SPATIAL_REF_SYS))
            return geometry;

        try {
            CoordinateReferenceSystem sourceSystem = CRS.parseWKT(referenceSystemDao.get(parameters.getSpatialKey()).getWkt());
            MathTransform transform = CRS.findMathTransform(sourceSystem, targetSystem, true);

            return JTS.transform(geometry, transform);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
