package org.w2fc.geoportal.ws.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.gis.GeoObjectUI;
import org.w2fc.geoportal.layer.GeoLayerUIAdapter;

public class GeoObjectFullAdapter extends GeoObjectUI {

	  private final HashSet<GeoObjectTag> tags;

	public GeoObjectFullAdapter() {
		tags = new HashSet<GeoObjectTag>();
	}

	public GeoObjectFullAdapter(GeoObject o) {
	        super(o);
	        tags = new HashSet<GeoObjectTag>(geoObject.getTags());
	    }
	    
	    @XmlElement
	    public String getCreator() {
	        return geoObject.getCreatedBy().getFullName();
	    }

	    @XmlElement
	    public String getEditor() {
	    	if(geoObject.getChangedBy() != null){
	    		return geoObject.getChangedBy().getFullName();
	    	}else{
	    		return "";
	    	}
	    }

	    @XmlElementWrapper(name="tags")
	    @XmlElement(name="tag")
	    public HashSet<GeoObjectTag> getTagModel() {
	        return tags;
	    }
	    
	    public String getLineColor() {
	    	if(geoObject.getGeoObjectProperties() != null){
	    		return geoObject.getGeoObjectProperties().getLineColor();
	    	}else{
	    		return null;
	    	}
	    }

	    public Boolean getIcon() {
	    	if(geoObject.getGeoObjectProperties() != null){
	    		return geoObject.getGeoObjectProperties().getIcon() != null;
	    	}else{
	    		return false;
	    	}
	    }

	    public String getFillColor() {
	    	if(geoObject.getGeoObjectProperties() != null){
	    		return geoObject.getGeoObjectProperties().getFillColor();
	    	}else{
	    		return null;
	    	}
	    }
	    

	    public Integer getLineWeight() {
	    	if(geoObject.getGeoObjectProperties() != null){
	    		return geoObject.getGeoObjectProperties().getLineWeight();
	    	}else{
	    		return null;
	    	}
	    }

	   public List<GeoLayerUIAdapter> getLayers(){
		   return null;
	   }


	@Override
	public List<Map<String, String>> getTags() {
		return null;
	}
	    
}