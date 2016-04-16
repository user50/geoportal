package org.w2fc.geoportal.gis;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.geotools.geojson.geom.GeometryJSON;
import org.w2fc.geoportal.domain.GeoObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

//@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/layers/GetObjectResponse")
public abstract class GeoObjectUI {

	@XmlTransient
	protected GeoObject geoObject = null;

	public GeoObjectUI() {
	}

	public GeoObjectUI(GeoObject obj) {
		geoObject = obj;
	}

	@XmlElement
	public Date getCreatedDateTime() {
		return geoObject.getCreated();
	}

	@XmlElement
	public Date getChangedDateTime() {
		return geoObject.getChanged();
	}

	@XmlElement
	public Long getId() {
		return geoObject.getId();
	}

	@XmlElement
	public String getName() {
		return geoObject.getName();
	}

	@XmlElement
	public Double getLat() {
		return geoObject.getTheGeom().getCoordinate().y;
	}

	@XmlElement
	public Double getLon() {
		return geoObject.getTheGeom().getCoordinate().x;
	}

	@XmlElement
	public String getWkt() {
		return geoObject.getTheGeom().toText();
	}

	@XmlElement
	public String getExtSysId() {
		return geoObject.getExtSysId();
	}

	@XmlElement
	public String getGuid() {
		return geoObject.getGuid();
	}

	@XmlElement
	public String getGeoJSON() throws ParseException, IOException {
		Geometry g1 = new WKTReader().read(getWkt());
		GeometryJSON g = new GeometryJSON(10);
		StringWriter writer = new StringWriter();
		g.write(g1, writer);
		return writer.toString();
	}

	@XmlTransient
	public abstract String getCreator();

	@XmlTransient
	public abstract String getEditor();

	@XmlTransient
	public abstract List<Map<String, String>> getTags();

	@XmlTransient
	public abstract String getLineColor();

	@XmlTransient
	public abstract Boolean getIcon();

	@XmlTransient
	public abstract String getFillColor();

	@XmlTransient
	public abstract Integer getLineWeight();

	@XmlElement
	public String getObjectGeomType() {
		return geoObject.getTheGeom().getGeometryType();
	}

	@XmlElement
	public String getFiasCode() {
		return geoObject.getFiasCode();
	}

	public void setFiasCode(String fias) {
		geoObject.setFiasCode(fias);
	}	
}
