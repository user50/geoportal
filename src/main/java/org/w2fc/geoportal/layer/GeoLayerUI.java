package org.w2fc.geoportal.layer;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoLayerMetadata;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://ws.portal.maps.yarcloud.ru/layers/GetLayersResponse")
public class GeoLayerUI {
	
	@XmlTransient
	protected GeoLayer layer = null;
	
	@XmlTransient
	protected GeoLayerMetadata metadata = null;

	public GeoLayerUI() {
	}

	public GeoLayerUI(GeoLayer layer) {
		this.layer = layer;
		if(layer.getMetadata() != null){
			this.metadata = layer.getMetadata();
		}else{
			this.metadata = new GeoLayerMetadata();
		}
	}

	// =============================================

	@XmlElement
	public Long getId() {
		return layer.getId();
	}

	@XmlElement
	public String getName() {
		return layer.getName();
	}

	@XmlElement
	public Long getParentId() {
		return layer.getParentId();
	}

	@XmlElement
	public Long getTypeId() {
		return layer.getTypeId();
	}

	@XmlElement
	public String getUrl() {
		return layer.getUrl();
	}

	@XmlElement
	public String getLineColor() {
		return layer.getLineColor();
	}

	@XmlElement
	public Integer getLineWeight() {
		return layer.getLineWeight();
	}

	@XmlElement
	public String getFillColor() {
		return layer.getFillColor();
	}

	@XmlElement
	public Integer getFillOpacity() {
		return layer.getFillOpacity();
	}

	@XmlElement
	public Integer getVersion() {
		return layer.getVersion();
	}

	@XmlElement
	public Boolean getViewByObject() {
		return metadata.getViewByObject();
	}

	@XmlElement
	public String getOwnerCompany() {
		return metadata.getOwnerCompany();
	}

	@XmlElement
	public String getOwnerName() {
		return metadata.getOwnerName();
	}

	@XmlElement
	public String getOwnerEmail() {
		return metadata.getOwnerEmail();
	}

	@XmlElement
	public String getOwnerPhone() {
		return metadata.getOwnerPhone();
	}

	@XmlElement
	public String getDescSpatialData() {
		return metadata.getDescSpatialData();
	}

	@XmlElement
	public String getSourceSpatialData() {
		return metadata.getSourceSpatialData();
	}

	@XmlElement
	public String getDocRegulation() {
		return metadata.getDocRegulation();
	}
	@XmlElement
	public String getAccessLevel() {
		return metadata.getAccessLevel();
	}
	@XmlElement
	public String getAccessConditions() {
		return metadata.getAccessConditions();
	}
	@XmlElement
	public String getMapAccuracy() {
		return metadata.getMapAccuracy();
	}

	@XmlElement
	public Date getLastUpdateMetadata() {
		return metadata.getLastUpdateMetadata();
	}

	@XmlElement
	public String getLastUpdateSpatialData() {
		return metadata.getLastUpdateSpatialData();
	}

	@XmlElement
	public String getUpdateFrequency() {
		return metadata.getUpdateFrequency();
	}
	@XmlElement
	public String getCoordinateSystem() {
		return metadata.getCoordinateSystem();
	}

	@XmlElement
	public String getCoverageArea() {
		return metadata.getCoverageArea();
	}

	@XmlElement
	public String getDataAmount() {
		return metadata.getDataAmount();
	}
	@XmlElement
	public String getExportFormat() {
		return metadata.getExportFormat();
	}


	@XmlElement
	public Date getChanged() {
		return metadata.getChanged();
	}
	@XmlElement
	public String getTmpl() {
		return (layer.getPopupTemplate() != null) ? layer.getPopupTemplate()
				.getTemplate() : null;
	}
	@XmlElement
	public Integer getPermissions() {
		return this.layer.getPermissions();
	}


}
