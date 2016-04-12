package org.w2fc.geoportal.ws.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.gis.GeoObjectUI;
import org.w2fc.geoportal.layer.GeoLayerUIAdapter;

public class GeoObjectFullAdapter extends GeoObjectUI {

	  private final HashSet<GeoObjectTag> tags;
	private List<GeoLayerUIAdapter> layers;

	/*public GeoObjectFullAdapter() {
		tags = new HashSet<GeoObjectTag>();
	}*/

	public GeoObjectFullAdapter(GeoObject o) {
	        super(o);
	        tags = new HashSet<GeoObjectTag>(geoObject.getTags());
	        if (geoObject.getGeoLayers() != null){
			   layers = new ArrayList<GeoLayerUIAdapter>();
			   for (GeoLayer geoLayer : geoObject.getGeoLayers()) {
				   layers.add(new GeoLayerUIAdapter(geoLayer));
			   }
	        }
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
		  
		   return layers;
	   }

	public String getGuid(){
		return geoObject.getGuid();
	}

	public String getExtSysId(){
		return geoObject.getExtSysId();
	}

	@Override
	public List<Map<String, String>> getTags() {
		return null;
	}
	    
}
