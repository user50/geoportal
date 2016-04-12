package org.w2fc.geoportal.wms;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.AnchorPoint;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Font;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointPlacement;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.TextSymbolizer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoSettings;
import org.w2fc.geoportal.gis.GeoObjectUI;
import org.w2fc.geoportal.utils.ServiceRegistry;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@Service
@Transactional(readOnly = true)
public class GeoProcessorImpl implements GeoProcessor {

	@Autowired
	protected ServiceRegistry serviceRegistry;

	private ThreadLocal<ServletContext> locals = new ThreadLocal();

	protected static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
	protected static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

	final Logger logger = LoggerFactory.getLogger(GeoProcessorImpl.class);

	private SimpleFeatureType SHAPE_TYPE;
	
	public GeoProcessorImpl() {
		
		try {
			SHAPE_TYPE = DataUtilities.createType("ShapeLayer",
					"geom:Geometry,centroid:Point,line:LineString,name:String");
		} catch (SchemaException e) {
			logger.error(e.getLocalizedMessage(), e);
			SHAPE_TYPE = null;
		}
	}
	
	@Override
	public List<Layer> getWmsLayers(Long layerId, ServletContext servletContext, ParamsContainer params) {
		GeoLayer layer = serviceRegistry.getLayerDao().getDetached(layerId);
		//if(layer.getMetadata().getViewByObject()) {
			//TODO: make styled
			return getWmsLayerNotStyled(layer, servletContext, params);
		/*} else {
			return getWmsLayerNotStyled(layer, servletContext, zoom);
		}*/
	}
	
	private List<Layer> getWmsLayerNotStyled(GeoLayer layer, ServletContext servletContext, ParamsContainer params) {
		locals.set(servletContext);
		DefaultFeatureCollection shapeCollection = new DefaultFeatureCollection(
				null, SHAPE_TYPE);
	
		SimpleFeatureBuilder shapeBuilder = new SimpleFeatureBuilder(SHAPE_TYPE);
	
		List<GeoObjectUI> gisObjects = getObjectByLayer(layer.getId(), params);
		if(gisObjects.isEmpty() /*size() == 0*/) {
			return new ArrayList();
		}
		/*GeoSettings factorsetting = serviceRegistry.getGeoSettingsDao().getByName("GENERALIZATION_FACTOR");
		Float factor = 100f;
		try{
			factor = Float.valueOf(factorsetting.getValue());
		}catch(Exception e){
			logger.debug("Set GENERALIZATION_FACTOR parameter well!");
		}*/
		//Float[] zoom = new Float[]{new Float(params.maxx - params.minx), new Float(params.maxy - params.miny)};
		
		for (GeoObjectUI obj : gisObjects) {
			Geometry g1;
			try {
				g1 = new WKTReader().read(obj.getWkt());
			
				if ( "Point".equals(obj.getObjectGeomType()) ) {
					shapeBuilder.set("centroid",g1);
				} else if ( "LineString".equals(obj.getObjectGeomType()) ) {
					shapeBuilder.set("line",g1);
				} else {
					Envelope env = g1.getEnvelopeInternal();
					/*if ( zoom[0] / factor > (env.getMaxX() - env.getMinX()) ||  
						 zoom[1] / factor > (env.getMaxY() - env.getMinY())) {
						continue;
					}*/
					shapeBuilder.set("geom",g1);
				}
				shapeBuilder.set("name",obj.getName());
				SimpleFeature feature = shapeBuilder.buildFeature(null);
				shapeCollection.add(feature);
				
			} catch (ParseException e) {
				logger.error(e.getLocalizedMessage());
			}
		}
		
		Style shapeStyle = createShapeStyle(layer, servletContext);
		List<Layer> res = new ArrayList();
		if(!shapeCollection.isEmpty() /*size() > 0*/) {
			res.add(new FeatureLayer(shapeCollection, shapeStyle));
		}

		return res;
	}
	
	public List<GeoObjectUI> getObjectByLayer(Long layerId, ParamsContainer params){
		
		GeoSettings factorsetting = serviceRegistry.getGeoSettingsDao().getByName("GENERALIZATION_FACTOR");
		Float factor = 100f;
		try{
			factor = Float.valueOf(factorsetting.getValue());
		}catch(Exception e){
			logger.debug("Set GENERALIZATION_FACTOR parameter well!");
		}
		
		return ObjectFactory.createGeoObjectUIAdapterListLite(serviceRegistry.getGeoObjectDao().listByLayerIdGeneralized(layerId, params, factor));
//		return ObjectFactory.createGeoObjectUIAdapterListLite(serviceRegistry.getGeoObjectDao().listByLayerIdCached(layerId)); // TODO
	}
	
	private Style createShapeStyle(GeoLayer layer, ServletContext servletContext) {
		Stroke stroke = styleFactory.createStroke(
				filterFactory.literal(layer.getLineColor()),
				filterFactory.literal(layer.getLineWeight()));
		Rule rule = styleFactory.createRule();
		Double opacity = (layer.getFillOpacity() == null) ? 0.2 : Double.valueOf(layer.getFillOpacity()) / 100;
		
		Fill fill = styleFactory.createFill(filterFactory.literal(layer.getFillColor()), filterFactory.literal(opacity));
		PolygonSymbolizer pSymbolizer = 
				styleFactory.createPolygonSymbolizer(stroke, fill, "geom");
		rule.symbolizers().add(pSymbolizer);
		
//text
		AnchorPoint anchorPoint = styleFactory.createAnchorPoint(filterFactory.literal(0.5),filterFactory.literal(0.5));
		int fontSize = layer.getLineWeight() * 5;
		PointPlacement pointPlacement = styleFactory.createPointPlacement(anchorPoint, null, filterFactory.literal(0));
		Fill textFill = styleFactory.createFill(filterFactory.literal(layer.getLineColor()));
		StyleBuilder styleBuilder = new StyleBuilder(); 
		TextSymbolizer textSymbolizerPoly = styleFactory.createTextSymbolizer(
							textFill,
							new Font[] {
									styleBuilder.createFont("Lucida Sans", fontSize),
									styleBuilder.createFont("Arial", fontSize)
							},
							styleBuilder.createHalo(Color.WHITE, 2),
							styleBuilder.attributeExpression("name"),
							pointPlacement,
							"geom");
		textSymbolizerPoly.getOptions().put(TextSymbolizer.POLYGONALIGN_KEY, "mbr");
		textSymbolizerPoly.getOptions().put(TextSymbolizer.GOODNESS_OF_FIT_KEY, "1.0");
		TextSymbolizer textSymbolizerLine = styleFactory.createTextSymbolizer(
				textFill,
				new Font[] {
						styleBuilder.createFont("Lucida Sans", fontSize),
						styleBuilder.createFont("Arial", fontSize)
				},
				styleBuilder.createHalo(Color.WHITE, 2),
				styleBuilder.attributeExpression("name"),
				pointPlacement,
				"line");
		textSymbolizerLine.getOptions().put(TextSymbolizer.FOLLOW_LINE_KEY, "true");
		rule.symbolizers().add(textSymbolizerPoly);
		rule.symbolizers().add(textSymbolizerLine);
//--text
//line
		
		Stroke strokeLine = styleFactory.createStroke(
				filterFactory.literal(layer.getLineColor()),
				filterFactory.literal(layer.getLineWeight()));
		LineSymbolizer line = styleFactory.createLineSymbolizer(strokeLine, "line");
		rule.symbolizers().add(line);
//---line
//marker
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

		rule.symbolizers().add(sym);
		
//--marker
		
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
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
