package org.w2fc.geoportal.imp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import nl.knaw.dans.common.dbflib.Field;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Table;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.geotools.data.FeatureReader;
import org.geotools.data.mif.MIFDataStore;
import org.geotools.data.mif.MIFFile;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.imp.GPXParser.GPXWaypoint;

import au.com.bytecode.opencsv.CSVReader;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class ImportUtils {

    private static final String POINT = "POINT";
    
    private static final String MULTIPOLYGON = "MULTIPOLYGON";
    
    private static final String POLYGON = "POLYGON";
    
    private static final String LINESTRING = "LINESTRING";
    

    static final String FIELD_GEOMETRY = "GEOMETRY";
    
    static final String FIELD_TYPE = "TYPE";
    
    static final String FIELD_NAME = "NAME";

    final static Logger logger = LoggerFactory.getLogger(ImportUtils.class);

    final static GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID
    
    /* 
     *  Format
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ImportObject{
        public String name = null;
        public String type = null;
        public String geometry = null;
        @XmlElement(name = "tag") List<ImportObjectTag> tags = new ArrayList<ImportObjectTag>();
        
        public GeoObject toGeoObject() throws ParseException{
            
             
            
            Geometry p = new WKTReader(factory).read(getWKT());
            
            GeoObject object = ObjectFactory.createGeoObject(name, p);
            
            for (ImportObjectTag t : tags) {
                object.getTags().add(ObjectFactory.createGeoObjectTag(object, t.name, t.value));
            }
            
            return object;
        }
        
        protected String getWKT(){
            return type.toUpperCase() + geometry;
        }
    }
    
    
    public static class ImportObjectTag{
        
        public ImportObjectTag(){}
        
        public ImportObjectTag(String name, String value){
            this.name = name;
            this.value = value;
        }
        
        @XmlAttribute
        public String name; 
        @XmlValue
        public String value;
    }
    
    
    /*
     *      Parse XML 
     */

    @XmlRootElement(name = "data")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Data{
        @XmlElement(name = "object", type = ImportObject.class)
        public List<ImportObject> objects = new ArrayList<ImportObject>();
    }

    public static List<GeoObject> fromXml(InputStream in) throws Exception{
        List<GeoObject> list = new ArrayList<GeoObject>();
        
        JAXBContext jaxbContext = JAXBContext.newInstance(Data.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Data data = (Data) jaxbUnmarshaller.unmarshal(in);
        
        List<ImportObject> l = data.objects;
        for (ImportObject importObject : l) {
            list.add(importObject.toGeoObject());
        }
        
        return list;
    }
    
    
    /*
     *      Parse DBF
     */
    
    public static List<GeoObject> fromDbf(String fileName) throws Exception{
        List<GeoObject> list = new ArrayList<GeoObject>();

        Table table = new Table(new File(fileName), "cp1251");
        try{
            table.open();

            List<Field> fields = table.getFields();
            Iterator<Record> iterator = table.recordIterator();

            while (iterator.hasNext()){
                Record record = iterator.next();

                ImportObject impObj = new ImportObject();
                
                impObj.name = record.getStringValue(FIELD_NAME).trim();
                impObj.type = record.getStringValue(FIELD_TYPE).trim();
                impObj.geometry = record.getStringValue(FIELD_GEOMETRY).trim();
                
                for (Field field : fields){
                    if(! FIELD_NAME.equals(field.getName()) && 
                        ! FIELD_TYPE.equals(field.getName()) &&
                        ! FIELD_GEOMETRY.equals(field.getName())){
                        
                        impObj.tags.add(new ImportObjectTag(field.getName(), record.getStringValue(field.getName()).trim()));
                    }
                }
            
                list.add(impObj.toGeoObject());
            }
        
        } finally{
            table.close();
        }
        return list;
    }
    
    
    /*
     *      Parse XLS 
     */
    
    public static List<GeoObject> fromXls(InputStream in) throws Exception{
        List<GeoObject> list = new ArrayList<GeoObject>();

        HSSFWorkbook workbook = new HSSFWorkbook(in);
        HSSFSheet sheet = workbook.getSheetAt(0);

        int rowCount = 0;
        List<String> headers = new ArrayList<String>();

        for (Row row : sheet) {
            if(1 == ++rowCount){   
                // read headers
                for (Cell cell : row){
                    headers.add(cell.getStringCellValue());
                }

            }else{
                // read data
                ImportObject impObj = new ImportObject();

                int cellCount = 0;
                for (Cell cell : row){
                    if(null == impObj.name && FIELD_NAME.equalsIgnoreCase(headers.get(cellCount))){
                        impObj.name = cell.getStringCellValue();

                    }else if(null == impObj.type && FIELD_TYPE.equalsIgnoreCase(headers.get(cellCount))){
                        impObj.type = cell.getStringCellValue();

                    }else if(null == impObj.geometry && FIELD_GEOMETRY.equalsIgnoreCase(headers.get(cellCount))){
                        impObj.geometry = cell.getStringCellValue();

                    }else{ // add tag

                        Object cellValue = null;

                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                cellValue = cell.getRichStringCellValue().getString();
                                break;
    
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    cellValue = cell.getDateCellValue();
                                } else {
                                    cellValue = new Double(cell.getNumericCellValue()).longValue();
                                }
                                break;
                                
                            case Cell.CELL_TYPE_BOOLEAN:
                                cellValue = cell.getBooleanCellValue();
                                break;
                                
                            case Cell.CELL_TYPE_FORMULA:
                                cellValue = cell.getCellFormula();
                                break;
                                
                            default:
                                cellValue = "";
                        }
                        impObj.tags.add(new ImportObjectTag(headers.get(cellCount), cellValue.toString()));
                    }

                    cellCount++;
                }

                list.add(impObj.toGeoObject());
            }
        }

        return list;
    }

    
    /*
     *      Parse CSV 
     */
    
    public static List<GeoObject> fromCsv(InputStream in) throws Exception{
        List<GeoObject> list = new ArrayList<GeoObject>();

        InputStreamReader in1 = new InputStreamReader(in, "cp1251");
        CSVReader reader = new CSVReader(in1, ';');

        try{
            String [] nextLine;
            int rowCount = 0;
            List<String> headers = new ArrayList<String>();
            while ((nextLine = reader.readNext()) != null) {
                if(1 == ++rowCount){   
                    // read headers
                    for (String h : nextLine){
                        headers.add(h);
                    }

                }else{
                    // read data
                    ImportObject impObj = new ImportObject();
                    
                    int cellCount = 0;
                    for (String string : nextLine) {
                        
                        if(cellCount > headers.size() - 1)
                            break;
                            
                        if(null == impObj.name && FIELD_NAME.equalsIgnoreCase(headers.get(cellCount))){
                            impObj.name = string;
                        
                        }else if(null == impObj.type && FIELD_TYPE.equalsIgnoreCase(headers.get(cellCount))){
                            impObj.type = string;
                        
                        }else if(null == impObj.geometry && FIELD_GEOMETRY.equalsIgnoreCase(headers.get(cellCount))){
                            impObj.geometry = string;
                        
                        }else{ // add tag
                            impObj.tags.add(new ImportObjectTag(headers.get(cellCount), string));
                        }
                        
                        cellCount++;
                    }
                    
                    list.add(impObj.toGeoObject());
                }
            }
            
        }finally{
            in1.close();
            reader.close();
        }
        
        return list;
    }
    
    
    /*
     *      Parse MIF/MID  
     */
    
    private static final String THE_GEOMETRY_MANGLE_FOR_MIF_MID = "_<<_The_Geometry_>>_";
    
    public static List<GeoObject> fromMifMid(String fileName) throws Exception{
        List<GeoObject> list = new ArrayList<GeoObject>();
       
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(MIFDataStore.PARAM_GEOMNAME, THE_GEOMETRY_MANGLE_FOR_MIF_MID);

        MIFFile file1 = new MIFFile(fileName, params);
        FeatureReader<SimpleFeatureType, SimpleFeature> fr =  file1.getFeatureReader();

        try{
            while (fr.hasNext()) {
                Feature in = fr.next();

                ImportObject impObj = new ImportObject(){
                    @Override protected String getWKT(){ImportUtils.logger.debug("MID/MIF: " + geometry); return geometry;}
                };

                impObj.geometry = in.getDefaultGeometryProperty().getValue().toString();

                if(impObj.geometry.contains(POINT)){
                    impObj.type = POINT;
                }else if(impObj.geometry.contains(POLYGON)){
                    impObj.type = POLYGON;
                }else if(impObj.geometry.contains(MULTIPOLYGON)){
                    impObj.type = MULTIPOLYGON;
                }else if(impObj.geometry.contains(LINESTRING)){
                    impObj.type = LINESTRING;
                }

                Collection<Property> col = in.getProperties();
                for (Property property : col) {
                    if(FIELD_NAME.equalsIgnoreCase(property.getName().toString())){
                        impObj.name = property.getValue().toString();

                    }else if(! THE_GEOMETRY_MANGLE_FOR_MIF_MID.equalsIgnoreCase(property.getName().toString())){
                    	Object val = property.getValue();
                    	if(val == null){
                    		impObj.tags.add(new ImportObjectTag(property.getName().toString(), ""));
                    	}else{
                    		impObj.tags.add(new ImportObjectTag(property.getName().toString(), property.getValue().toString()));
                    	}
                    }
                }
                
                list.add(impObj.toGeoObject());
            }

        }finally{
            fr.close();
        }

        return list;
    }
    
    
    private static int TEMP_DIR_ATTEMPTS = 100;
    
    public static File createTempDir() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = System.currentTimeMillis() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within "
                + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }


    public static List<File> extractZip(File file, File tempDir) throws FileNotFoundException, IOException {
        List<File> tmpFiles = new LinkedList<File>();

        ZipInputStream zin = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry;

        long nameMangle = System.currentTimeMillis();

        while ((entry = zin.getNextEntry()) != null) {
            File tempFile = new File(tempDir, "tmpImportMifMid" + nameMangle + entry.getName());
            tmpFiles.add(tempFile);

            BufferedOutputStream out = new BufferedOutputStream (new FileOutputStream(tempFile));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = zin.read(buffer)) >= 0){
                out.write(buffer, 0, len);
            }
            out.close();
            zin.closeEntry();
        }

        zin.close();

        return tmpFiles;
    }


	public static List<GeoObject> fromGpx(String name, InputStream is) throws IOException {
		GPXParser parser = new GPXParser(new BufferedReader(new InputStreamReader(is)));
		ArrayList<GPXWaypoint> points = parser.getWaypoints();
		List<GeoObject> list = new ArrayList<GeoObject>();
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID
		Coordinate[] coordinates = new Coordinate[points.size()];
		for(int i = 0 ; i < points.size() ; i++){
			coordinates[i] = new Coordinate(points.get(i).getLongitude(), points.get(i).getLatitude());
		}
	    CoordinateArraySequence coords = new CoordinateArraySequence(coordinates);
	    LineString p = new LineString(coords, factory);
	    list.add(ObjectFactory.createGeoObject(name, p));
	    return list;
	}


	public static List<GeoObject> fromJPG(String name, InputStream is) throws Exception {
		List<GeoObject> list = new ArrayList<GeoObject>();
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);// SRID

		Metadata metadata = JpegMetadataReader.readMetadata(is);
		GpsDirectory gps = metadata.getFirstDirectoryOfType(GpsDirectory.class);
		GeoLocation location = gps.getGeoLocation();
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		if (latitude == 0 && longitude == 0)
			throw new Exception("Geo tagging is not found!");
		Point p = factory.createPoint(new Coordinate(longitude, latitude));
		list.add(ObjectFactory.createGeoObject(name, p));
		return list;
	}
}
