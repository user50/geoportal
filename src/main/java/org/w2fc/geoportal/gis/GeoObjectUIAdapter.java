package org.w2fc.geoportal.gis;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.layer.GeoLayerUIAdapter;

public class GeoObjectUIAdapter extends GeoObjectUI{
    
    private List<Map<String, String>> tags = new LinkedList<Map<String, String>>();
	private List<GeoLayerUIAdapter> layers = null;
	
    public GeoObjectUIAdapter(GeoObject o) {
        super(o);
        for(GeoObjectTag t : geoObject.getTags()){
        	Map<String,String> tag = new HashMap<String, String>();
        	tag.put("id", t.getId().toString());
        	tag.put("key", t.getKey());
        	tag.put("value", t.getValue());
        	tags.add(tag);
        }
        layers = ObjectFactory.createGeoLayerUIAdapterList(geoObject.getGeoLayers());
    }
    

    public String getCreator() {
        return geoObject.getCreatedBy().getFullName();
    }

    
    public String getEditor() {
    	if(geoObject.getChangedBy() != null){
    		return geoObject.getChangedBy().getFullName();
    	}else{
    		return "";
    	}
    }

    
   
    public List<Map<String, String>> getTags() {
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
    
    
}
