package org.w2fc.geoportal.layer;

import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

import org.w2fc.geoportal.domain.AddnsPopupTemplate;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoLayerMetadata;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class GeoLayerUIAdapter extends GeoLayerUI{

   private String url;

	private String wmsUrl;

	private String esriUrl;

	private Integer haveObjects;
	
	@XmlTransient
	private AddnsPopupTemplate tmpl;

	@XmlTransient
	private AddnsPopupTemplate esriTmpl;
    
    
    /* Default constructor for Spring MVC */
    public GeoLayerUIAdapter() {
    	super(new GeoLayer());
    }

    public GeoLayerUIAdapter(GeoLayer layer) {
        super(layer);
        
        // if metadata isn't available by some reason then put empty GeoLayerMetadata
        if(null == this.metadata)
            this.metadata = new GeoLayerMetadata();
        
        switch(getTypeId().intValue()){
        case 1:
        	tmpl = layer.getPopupTemplate();
        	break;
        case 3:
 			setUrl(layer.getUrl());
 			break;
 		case 2:
 			setWmsUrl(layer.getUrl());
 			break;
 		case 5:
 			setEsriUrl(layer.getUrl());
 			esriTmpl = layer.getPopupTemplate();
	}
    }
    
    

    @JsonIgnore
    public GeoLayer getGeoLayer() {
    	switch(getTypeId().intValue()){
    		case 1:
    			layer.setPopupTemplate(tmpl);
    			break;
    		case 3:
     			layer.setUrl(url);
     			break;
     		case 2:
     			layer.setUrl(wmsUrl);
     			break;
     		case 5:
     			layer.setUrl(esriUrl);
     			layer.setPopupTemplate(esriTmpl);
    	}
        return layer;
    }
    
    @JsonIgnore
    public GeoLayerMetadata getGeoLayerMetadata() {
        return metadata;
    }

    
    //=============================================
    
    public void setId(Long id) {
        layer.setId(id);
    }

    public void setName(String name) {
        layer.setName(name);
    }

    public void setParentId(Long parentId) {
        layer.setParentId(parentId);
    }

    public void setTypeId(Long typeId) {
        layer.setTypeId(typeId);
    }

    public int hashCode() {
        return layer.hashCode();
    }

    @Override
    public String getUrl() {
        switch(getTypeId().intValue()){
        	case 3:
        		return url;
        	case 2:
        		return wmsUrl;
        	case 5:
        		return esriUrl;
        }
    	return url;
    }

    public void setUrl(String url) {
    	this.url = url;
    }
    
    public String getWmsUrl() {
        return getUrl();
    }

    public void setWmsUrl(String url) {
    	wmsUrl = url;
    }
    
    public String getEsriUrl() {
    	 return getUrl();
    }

    public void setEsriUrl(String url) {
    	esriUrl = url;
    }

    public void setLineColor(String lineColor) {
        layer.setLineColor(lineColor);
    }

    public void setLineWeight(Integer lineWeight) {
        layer.setLineWeight(lineWeight);
    }

    public void setFillColor(String fillColor) {
        layer.setFillColor(fillColor);
    }

    

    public void setFillOpacity(Integer fillOpacity) {
        layer.setFillOpacity(fillOpacity);
    }

    

    public void setVersion(Integer version) {
        layer.setVersion(version);
    }

    
    public void setViewByObject(Boolean viewByObject) {
        metadata.setViewByObject(viewByObject);
    }

    

    public void setOwnerCompany(String ownerCompany) {
        metadata.setOwnerCompany(ownerCompany);
    }

    

    public void setOwnerName(String ownerName) {
        metadata.setOwnerName(ownerName);
    }

    

    public void setOwnerEmail(String ownerEmail) {
        metadata.setOwnerEmail(ownerEmail);
    }

    

    public void setOwnerPhone(String ownerPhone) {
        metadata.setOwnerPhone(ownerPhone);
    }

    

    public void setDescSpatialData(String descSpatialData) {
        metadata.setDescSpatialData(descSpatialData);
    }

    

    public void setSourceSpatialData(String sourceSpatialData) {
        metadata.setSourceSpatialData(sourceSpatialData);
    }

    

    public void setDocRegulation(String docRegulation) {
        metadata.setDocRegulation(docRegulation);
    }

    

    public void setAccessLevel(String accessLevel) {
        metadata.setAccessLevel(accessLevel);
    }

    

    public void setAccessConditions(String accessConditions) {
        metadata.setAccessConditions(accessConditions);
    }

    

    public void setMapAccuracy(String mapAccuracy) {
        metadata.setMapAccuracy(mapAccuracy);
    }

    

    public void setLastUpdateMetadata(Date lastUpdateMetadata) {
        metadata.setLastUpdateMetadata(lastUpdateMetadata);
    }

    

    public void setLastUpdateSpatialData(String lastUpdateSpatialData) {
        metadata.setLastUpdateSpatialData(lastUpdateSpatialData);
    }

    

    public void setUpdateFrequency(String updateFrequency) {
        metadata.setUpdateFrequency(updateFrequency);
    }

    

    public void setCoordinateSystem(String coordinateSystem) {
        metadata.setCoordinateSystem(coordinateSystem);
    }

    

    public void setCoverageArea(String coverageArea) {
        metadata.setCoverageArea(coverageArea);
    }

    

    public void setDataAmount(String dataAmount) {
        metadata.setDataAmount(dataAmount);
    }

    

    public void setExportFormat(String exportFormat) {
        metadata.setExportFormat(exportFormat);
    }

    public void setGeoLayer(GeoLayer geoLayer) {
        metadata.setGeoLayer(geoLayer);
    }

    

    public void setChanged(Date changed) {
        metadata.setChanged(changed);
    }
    
    public Long getTmplId(){
    	 switch(getTypeId().intValue()){
     		case 1:
     			return (tmpl != null)?tmpl.getId():-1;
     		case 5:
     			return (esriTmpl != null)?esriTmpl.getId():-1;
    	 }
    	 return (tmpl != null)?tmpl.getId():-1;
    }
    
    public void setTmplId(Long id){
    	if(id == null)return;
    	tmpl = new AddnsPopupTemplate();
    	tmpl.setId(id);
		//layer.setPopupTemplate(tmpl);
    }
    
    public Long getEsriTmplId(){
    	return getTmplId();
    }
    
    public void setEsriTmplId(Long id){
    	if(id == null)return;
    	esriTmpl = new AddnsPopupTemplate();
    	esriTmpl.setId(id);
    }
    
    public void setPermissions(Integer permissions) {
        this.layer.setPermissions(permissions);
    }

	public Integer getHaveObjects() {
		return haveObjects;
	}

	public void setHaveObjects(Integer haveObjects) {
		this.haveObjects = haveObjects;
	}
}
