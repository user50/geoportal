package org.w2fc.geoportal.wms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;

import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
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
import org.w2fc.geoportal.utils.ServiceRegistry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

@Service
public class AreaProcessor {

	@Autowired
	protected ServiceRegistry serviceRegistry;

	final Logger logger = LoggerFactory.getLogger(AreaProcessor.class);

	protected SimpleFeatureType TYPE;

	protected static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
	protected static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

	private final static GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);// SRID

	public AreaProcessor() {
		try {
			TYPE = DataUtilities.createType("Layer", "geom:Polygon,name:String");
		} catch (SchemaException e) {
			logger.error(e.getLocalizedMessage(), e);
			TYPE = null;
		}
	}

	public Collection<Layer> getAreaObject(Geometry permArea, Geometry permAreaIslands, ServletContext servletContext) {

		DefaultFeatureCollection collection = new DefaultFeatureCollection(null, TYPE);
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(TYPE);

		builder.add(permArea);

		SimpleFeature feature = builder.buildFeature(null);
		collection.add(feature);

		Style style = createStyle();
		Layer pLayer = new FeatureLayer(collection, style);

		List<Layer> result = new ArrayList<Layer>();

		result.add(pLayer);

		if (permAreaIslands != null) {
			GeometryCollection islands = (GeometryCollection) permAreaIslands;
			for (int i = 0; i < islands.getNumGeometries(); i++) {
				collection = new DefaultFeatureCollection(null, TYPE);
				builder = new SimpleFeatureBuilder(TYPE);
				builder.add(islands.getGeometryN(i));
				feature = builder.buildFeature(null);
				collection.add(feature);
				Layer iLayer = new FeatureLayer(collection, style);
				result.add(iLayer);
			}
		}

		return result;
	}

	private Style createStyle() {
		Stroke stroke = styleFactory.createStroke(filterFactory.literal("#FF0000"), filterFactory.literal(5));
		// LineSymbolizer line = styleFactory.createLineSymbolizer(stroke,
		// "geom");
		Rule rule = styleFactory.createRule();
		// rule.symbolizers().add(line);

		Fill fill = styleFactory.createFill(filterFactory.literal("#D4D4D4"), filterFactory.literal(1));
		PolygonSymbolizer pSymbolizer = styleFactory.createPolygonSymbolizer(stroke, fill, null);
		rule.symbolizers().add(pSymbolizer);
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}
}
