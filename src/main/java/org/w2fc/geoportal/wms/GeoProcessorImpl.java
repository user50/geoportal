package org.w2fc.geoportal.wms;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.gis.GeoObjectUI;
import org.w2fc.geoportal.utils.ServiceRegistry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@Service
@Transactional
public class GeoProcessorImpl implements GeoProcessor {

	@Autowired
	protected ServiceRegistry serviceRegistry;

	private ThreadLocal<ServletContext> locals = new ThreadLocal<ServletContext>();

	protected static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
	protected static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
	
	final Logger logger = LoggerFactory.getLogger(GeoProcessorImpl.class);

	private SimpleFeatureType POINT_TYPE;
	private SimpleFeatureType SHAPE_TYPE;
	private SimpleFeatureType LINE_TYPE;
	
	public GeoProcessorImpl() {
		try {
			POINT_TYPE = DataUtilities.createType("PointLayer",
					"centroid:Point,name:String");
		} catch (SchemaException e) {
			logger.error(e.getLocalizedMessage(), e);
			POINT_TYPE = null;
		}
		try {
			SHAPE_TYPE = DataUtilities.createType("ShapeLayer",
					"geom:Geometry,name:String");
		} catch (SchemaException e) {
			logger.error(e.getLocalizedMessage(), e);
			SHAPE_TYPE = null;
		}
		try {
			LINE_TYPE = DataUtilities.createType("LineLayer",
					"geom:LineString,name:String");
		} catch (SchemaException e) {
			logger.error(e.getLocalizedMessage(), e);
			LINE_TYPE = null;
		}
	}
	
	@Override
	public List<Layer> getWmsLayers(Long layerId, ServletContext servletContext) {
		GeoLayer layer =  serviceRegistry.getLayerDao().get(layerId);
		if(layer.getMetadata().getViewByObject()){
			//TODO: make styled
			return getWmsLayerNotStyled(layer, servletContext);
		}else{
			return getWmsLayerNotStyled(layer, servletContext);
		}
	}
	
	private List<Layer> getWmsLayerNotStyled(GeoLayer layer, ServletContext servletContext) {
		locals.set(servletContext);
		DefaultFeatureCollection pointCollection = new DefaultFeatureCollection(
				null, POINT_TYPE);
		DefaultFeatureCollection shapeCollection = new DefaultFeatureCollection(
				null, SHAPE_TYPE);
		DefaultFeatureCollection lineCollection = new DefaultFeatureCollection(
				null, LINE_TYPE);

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

		SimpleFeatureBuilder pointBuilder = new SimpleFeatureBuilder(POINT_TYPE);
		SimpleFeatureBuilder shapeBuilder = new SimpleFeatureBuilder(SHAPE_TYPE);
		SimpleFeatureBuilder lineBuilder = new SimpleFeatureBuilder(LINE_TYPE);

		List<GeoObjectUI> gisObjects = getObjectByLayer(layer.getId());
		if(gisObjects.size() == 0)return null;
		for (GeoObjectUI obj : gisObjects) {
			Geometry g1;
			try {
				g1 = new WKTReader().read(obj.getWkt());
			
			if("Point".equals(obj.getObjectGeomType())){
				pointBuilder.add(g1);
				pointBuilder.add(obj.getName());
				// featureBuilder.add(node.getId());
				SimpleFeature feature = pointBuilder.buildFeature(null);
				pointCollection.add(feature);
				continue;
			}
			if("LineString".equals(obj.getObjectGeomType())){
				lineBuilder.add(g1);
				lineBuilder.add(obj.getName());
				SimpleFeature feature = lineBuilder.buildFeature(null);
				lineCollection.add(feature);
				continue;
			}
			
				shapeBuilder.add(g1);
				shapeBuilder.add(obj.getName());
				// featureBuilder.add(node.getId());
				SimpleFeature feature = shapeBuilder.buildFeature(null);
				shapeCollection.add(feature);
			
			} catch (ParseException e) {
				logger.error(e.getLocalizedMessage());
			}
		}
			
		Style pointStyle = createPointStyle(layer, servletContext);
		Style shapeStyle = createShapeStyle(layer);
		Style lineStyle = createLineStyle(layer);
		List<Layer> res = new ArrayList<Layer>();
		if(pointCollection.size() > 0)res.add(new FeatureLayer(pointCollection, pointStyle));
		if(shapeCollection.size() > 0)res.add(new FeatureLayer(shapeCollection, shapeStyle));
		if(lineCollection.size() > 0)res.add(new FeatureLayer(lineCollection, lineStyle));
		return res;
	}
	
	public List<GeoObjectUI> getObjectByLayer(Long layerId){
		return ObjectFactory.createGeoObjectUIAdapterListLite(serviceRegistry.getGeoObjectDao().listByLayerIdCached(layerId));
	}
	
	private Style createShapeStyle(GeoLayer layer) {
		Stroke stroke = styleFactory.createStroke(
				filterFactory.literal(layer.getLineColor()),
				filterFactory.literal(layer.getLineWeight()));
		//LineSymbolizer line = styleFactory.createLineSymbolizer(stroke, "geom");
		Rule rule = styleFactory.createRule();
		//rule.symbolizers().add(line);
		Double opacity = (layer.getFillOpacity() == null)?0.2:Double.valueOf(layer.getFillOpacity())/100;
		
		Fill fill = styleFactory.createFill(filterFactory.literal(layer.getFillColor()), filterFactory.literal(opacity));
		PolygonSymbolizer pSymbolizer = 
			    styleFactory.createPolygonSymbolizer(stroke, fill, null);
		rule.symbolizers().add(pSymbolizer);
		FeatureTypeStyle fts = styleFactory
				.createFeatureTypeStyle(new Rule[] { rule });
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}
	
	private Style createLineStyle(GeoLayer layer) {
		Stroke stroke = styleFactory.createStroke(
				filterFactory.literal(layer.getLineColor()),
				filterFactory.literal(layer.getLineWeight()));
		LineSymbolizer line = styleFactory.createLineSymbolizer(stroke, "geom");
		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(line);
		
		FeatureTypeStyle fts = styleFactory
				.createFeatureTypeStyle(new Rule[] { rule });
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}
	
	private Style createPointStyle(GeoLayer layer, ServletContext servletContext) {
		Graphic gr = styleFactory.createDefaultGraphic();
		Icon flagIcon = null;
			try {
				BufferedImage image = ImageIO.read(layer.getIcon().getBinaryStream());
				flagIcon = getFlagImage(image);
			}catch(Exception e){
				logger.debug(e.getLocalizedMessage());	
				BufferedImage image = errorIcon(servletContext);
				flagIcon = getFlagImage(image);
			}
		
		
		ExternalGraphic external = styleFactory.createExternalGraphic(flagIcon, "image/png");

		gr.graphicalSymbols().clear();
		gr.graphicalSymbols().add(external);
		// gr.setSize(filterFactory.literal(5));

		PointSymbolizer sym = styleFactory
				.createPointSymbolizer(gr, "centroid");

		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);

		FeatureTypeStyle fts = styleFactory
				.createFeatureTypeStyle(new Rule[] { rule });
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}



private Icon getFlagImage(BufferedImage image) {
	try {
		int w = 25;
		int h = 41;
		BufferedImage combined = new BufferedImage(w, h * 2,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = combined.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		// paint both images, preserving the alpha channels
		g.drawImage(image, 0, 0, w, h, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		return new ImageIcon(combined);
	} catch (Exception e) {
		logger.error(e.getLocalizedMessage(), e);
		return null;
	}
}


private BufferedImage errorIcon(ServletContext servletContext) {
	try {
		BufferedImage image = ImageIO.read(new File(servletContext
				.getRealPath("/css/images/error-icon.png")));
		return image;
	} catch (IOException e) {
		logger.error(e.getLocalizedMessage(), e);
		return null;
	}
}


}
