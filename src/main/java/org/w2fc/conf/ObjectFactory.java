package org.w2fc.conf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.w2fc.geoportal.domain.GeoACL;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoLayerMetadata;
import org.w2fc.geoportal.domain.GeoLayerToUserReference;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectProperties;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.gis.GeoObjectUI;
import org.w2fc.geoportal.gis.GeoObjectUIAdapter;
import org.w2fc.geoportal.gis.GeoObjectUIAdapterEmptyGeom;
import org.w2fc.geoportal.gis.GeoObjectUIAdapterLite;
import org.w2fc.geoportal.gis.model.HistoryModel;
import org.w2fc.geoportal.layer.GeoLayerUIAdapter;
import org.w2fc.geoportal.ws.model.GeoObjectFullAdapter;

import com.vividsolutions.jts.geom.Geometry;


public class ObjectFactory {

	public static GeoObject createGeoObject(String name, SimpleFeature sf) {
				GeoObject object = new GeoObject();
		    	Set<GeoObjectTag> objectTags = new HashSet<GeoObjectTag>();
		    	for(Property prop : sf.getProperties()){
		    		//String propName = prop.getName().toString();
		    		if(Geometry.class.isAssignableFrom(prop.getType().getBinding())){
		    			object.setTheGeom((Geometry)prop.getValue());
		    			continue;
		    		}
		    		if(prop.getType().getBinding().isAssignableFrom(String.class)){
		    			//tags
		    			if("name".equals(prop.getName().toString()))continue;
		    			GeoObjectTag tag = new GeoObjectTag();
		    			tag.setKey(prop.getName().toString());
		    			tag.setValue(prop.getValue().toString());
		    			tag.setGeoObject(object);
		    			objectTags.add(tag);
		    		}
		    	}
		    	object.setName(name);
		    	object.setTags(objectTags);
		    	return object;
	}
	
	public static GeoObject createGeoObject(String name, Geometry geom) {
		GeoObject object = new GeoObject();
		object.setTheGeom(geom);
    	object.setName(name);
    	Set<GeoObjectTag> objectTags = new HashSet<GeoObjectTag>();
    	object.setTags(objectTags);
    	return object;
}

	public static GeoObjectProperties createGeoObjectProperties(
			GeoObject geoObject) {
		GeoObjectProperties props = new GeoObjectProperties();
		props.setGeoObject(geoObject);
		geoObject.setGeoObjectProperties(props);
		return props;
	}

	public static List<GeoLayerUIAdapter> createGeoLayerUIAdapterList(
			Collection<GeoLayer> list) {
		if(list == null || list.size() == 0)return new ArrayList<GeoLayerUIAdapter>();
		List<GeoLayerUIAdapter> result = new ArrayList<GeoLayerUIAdapter>(list.size());
		for(GeoLayer layer : list){
			result.add(new GeoLayerUIAdapter(layer));
		}
		return result;
	}

	public static List<GeoObjectUI> createGeoObjectUIAdapterList(
			Collection<GeoObject> list) {
		List<GeoObjectUI> res = new ArrayList<GeoObjectUI>(list.size()); 
        for (GeoObject geoObject : list) {
            res.add(new GeoObjectUIAdapter(geoObject));
        }
		return res;
	}
	
	public static List<GeoObjectUI> createGeoObjectUIAdapterListLite(
			Collection<GeoObject> list) {
		List<GeoObjectUI> res = new ArrayList<GeoObjectUI>(list.size()); 
        for (GeoObject geoObject : list) {
            res.add(new GeoObjectUIAdapterLite(geoObject));
        }
		return res;
	}

	public static List<GeoObjectUI> createGeoObjectUIAdapterListLiteWithoutGeom(
			List<GeoObject> list) {
		List<GeoObjectUI> res = new ArrayList<GeoObjectUI>(list.size()); 
        for (GeoObject geoObject : list) {
            res.add(new GeoObjectUIAdapterEmptyGeom(geoObject));
        }
		return res;
	}
	
	public static List<GeoObjectFullAdapter> createGeoObjectFullAdapterList(
			Collection<GeoObject> list) {
		List<GeoObjectFullAdapter> res = new ArrayList<GeoObjectFullAdapter>(list.size()); 
        for (GeoObject geoObject : list) {
            res.add(new GeoObjectFullAdapter(geoObject));
        }
		return res;
	}
	
	public static GeoLayer createGeoLayer() {
		GeoLayer layer = new GeoLayer();
		GeoLayerMetadata metadata = new GeoLayerMetadata();
		layer.setMetadata(metadata);
        metadata.setGeoLayer(layer);
        layer.setLayerToUserReferences(new HashSet<GeoLayerToUserReference>());
		return layer;
	}

	public static GeoObjectTag createGeoObjectTag(GeoObject object,
			String key, String value) {
		GeoObjectTag tag = new GeoObjectTag();
		tag.setKey(key);
		tag.setValue(value);
		tag.setGeoObject(object);
		return tag;
	}

	public static GeoACL createGeoACL(GeoObject obj) {
		GeoACL acl = new GeoACL();
		acl.setName(obj.getName());
		acl.setGeoObject(obj);
		return acl;
	}
/*
	public static UserDetails createCustomUserDetails(GeoUser user) {
		Set<GeoUserRole> roles = user.getGeoUserRoles();
		
		Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
		for(GeoUserRole role : roles){
			auths.add(new SimpleGrantedAuthority(role.getName()));
		}
		if(auths.size() == 0)auths = AuthorityUtils.NO_AUTHORITIES;
		
		CustomUserDetails details = new CustomUserDetails(user.getId(), user.getFullName(), user.getLogin(), user.getPassword(), 
				user.getEnabled(), true, true, true, auths);
		
		details.setRoles(roles);
		
		return details;
	}
*/

	public static List<HistoryModel> createHistoryModelAdapter(List<Object[]> historyList) {
		List<HistoryModel> result = new ArrayList<HistoryModel>(historyList.size());
		for(Object[] entity : historyList){
			GeoObject object =  (GeoObject) entity[0];
			DefaultRevisionEntity revision = (DefaultRevisionEntity) entity[1];
			RevisionType revType = (RevisionType) entity[2];
			HistoryModel history = new HistoryModel();
			history.setRevDate(revision.getRevisionDate());
			history.setRevTime(revision.getTimestamp());
			history.setRevId(revision.getId());
			history.setRevType(revType.name());
			result.add(history);
		}
		return result;
	}




}
