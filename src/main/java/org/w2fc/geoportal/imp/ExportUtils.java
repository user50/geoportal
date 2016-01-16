package org.w2fc.geoportal.imp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.geotools.data.FeatureWriter;
import org.geotools.data.mif.MIFDataStore;
import org.geotools.data.mif.MIFFile;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w2fc.geoportal.gis.GeoObjectUI;

import au.com.bytecode.opencsv.CSVWriter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class ExportUtils {
    
    final static Logger logger = LoggerFactory.getLogger(ExportUtils.class);
    

    public static File[] exportAsMifMid(List<GeoObjectUI> objectsWithTags, Set<String> keys, File tmpDir) throws Exception {

        File[] files = new File[]{};

        List<GeoObjectUI> points = new ArrayList<GeoObjectUI>();
        List<GeoObjectUI> lines = new ArrayList<GeoObjectUI>();
        List<GeoObjectUI> polygons = new ArrayList<GeoObjectUI>();
        List<GeoObjectUI> multipolygons = new ArrayList<GeoObjectUI>();

        for (GeoObjectUI co : objectsWithTags) {
            if("Point".equals(co.getObjectGeomType())){
                logger.debug(co.getWkt());
                points.add(co);

            }else if("LineString".equals(co.getObjectGeomType())){
                logger.debug(co.getWkt());
                lines.add(co);

            }if("Polygon".equals(co.getObjectGeomType())){
                logger.debug(co.getWkt());
                polygons.add(co);
            }if("MultiPolygon".equals(co.getObjectGeomType())){
                logger.debug(co.getWkt());
                multipolygons.add(co);
            }
        }

        if(0 != points.size())
            files = (File[]) ArrayUtils.addAll(files, importMifMidByGeometryType(points, keys, tmpDir, Point.class));
        if(0 != lines.size())
            files = (File[]) ArrayUtils.addAll(files, importMifMidByGeometryType(lines, keys, tmpDir, LineString.class));
        if(0 != polygons.size())
            files = (File[]) ArrayUtils.addAll(files, importMifMidByGeometryType(polygons, keys, tmpDir, Polygon.class));
        if(0 != multipolygons.size())
            files = (File[]) ArrayUtils.addAll(files, importMifMidByGeometryType(multipolygons, keys, tmpDir, MultiPolygon.class));

        return files;
    }

    private static File[] importMifMidByGeometryType(List<GeoObjectUI> objectsWithTags, Set<String> keys, File tmpDir, Class<? extends Geometry> clazz )
            throws IOException, SchemaException, ParseException {
        SimpleFeatureTypeBuilder bilder = new SimpleFeatureTypeBuilder();

        //add a geometry property
        bilder.setCRS(DefaultGeographicCRS.WGS84); // set crs first
        bilder.add( "the_geom", clazz ); // then add geometry

        //set the name
        bilder.setName( "ExportFeatureType" );

        //add some properties
        bilder.add(ImportUtils.FIELD_NAME, String.class );
        
        for (String key : keys) {
            if(key.equalsIgnoreCase(ImportUtils.FIELD_NAME))
                continue;
            
            bilder.add( key, String.class );
        }
        
        // set default geometry property
//        b.setDefaultGeometry("the_geom");

        //build the type
        final SimpleFeatureType featureType = bilder.buildFeatureType();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(MIFDataStore.HCLAUSE_COORDSYS, DefaultGeographicCRS.WGS84.getCoordinateSystem().getName().getCode());
        params.put(MIFDataStore.HCLAUSE_CHARSET, "UTF-8");
        params.put(MIFDataStore.HCLAUSE_DELIMITER, ";");

        String path = tmpDir.getAbsolutePath() + File.separatorChar + clazz.getSimpleName();
        MIFFile file = new MIFFile(path +".mif", featureType, params);
        FeatureWriter<SimpleFeatureType, SimpleFeature> fw = file.getFeatureWriter();

        for (GeoObjectUI co : objectsWithTags) {
            Feature feature = fw.next();
            
            // set geometry
            GeometryAttribute theGeom = feature.getDefaultGeometryProperty();
            theGeom.setValue(new WKTReader().read(co.getWkt()));
            feature.setDefaultGeometryProperty(theGeom);
            
            // set tags
            List<Map<String, String>> tags1 = co.getTags();
            for (int i = 0; i < tags1.size(); i++) {
                feature.getProperty(tags1.get(i).get("key")).setValue(tags1.get(i).get("value"));
            }
            
            feature.getProperty(ImportUtils.FIELD_NAME).setValue(co.getName());
            
            fw.write();
        }
        
        fw.close();
        
        return new File[]{new File(path +".mif"), new File(path +".mid")};
    }

    
    public static File[] exportAsCsv(List<GeoObjectUI> objectsWithTags, Set<String> keys, File tmpDir, String ext) throws IOException {
        File file = new File(tmpDir.getAbsolutePath() + File.separatorChar +"export." + ext);
        CSVWriter csvOutput = new CSVWriter(
                new OutputStreamWriter(new FileOutputStream(file), "cp1251"), 
                ';', 
                CSVWriter.NO_QUOTE_CHARACTER);
        
        String[] keysArr = (String[]) ArrayUtils.addAll(new String[]{ImportUtils.FIELD_NAME, ImportUtils.FIELD_TYPE, ImportUtils.FIELD_GEOMETRY},  keys.toArray(new String[]{}));

        // header
        csvOutput.writeNext(keysArr);
        
        for (GeoObjectUI co : objectsWithTags) {
            
            String[] row = new String[keysArr.length + 1];
            row[0] = co.getName();
            
            if(null != co.getWkt()){
                String[] wkt = co.getWkt().split("\\(");
                row[1] = wkt[0].trim();
                row[2] = co.getWkt().replace(wkt[0], "");
            }else{
                row[1] = "";
                row[2] = "";
            }
            
            List<Map<String, String>> t = co.getTags();
            for (int i = 3; i < keysArr.length; i++) {
            	Map<String, String> tag = searchTagByKey(t, keysArr[i]);
                
                row[i] = (null != tag ? tag.get("value").replaceAll("'", "") : "");
            }
            
            csvOutput.writeNext(row);
        }
        
        csvOutput.close();
        
        return new File[]{file};
    }

    
    private static Map<String, String> searchTagByKey(List<Map<String, String>> t, String string) {
        for (int i = 0; i < t.size(); i++) {
            if(string.equalsIgnoreCase(t.get(i).get("key"))){
                return t.get(i);
            }
        }
        return null;
    }
}
