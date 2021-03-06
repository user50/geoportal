package org.w2fc.geoportal.auth;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w2fc.conf.Constants;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.gis.model.DeleteObjectsModel;
import org.w2fc.geoportal.gis.model.GeoObjectJSONModel;
import org.w2fc.geoportal.gis.model.PortalObjectModel;
import org.w2fc.geoportal.utils.ServiceRegistry;
import org.w2fc.geoportal.ws.exception.AccessDeniedException;
import org.w2fc.geoportal.ws.exception.GeoObjectNotFoundException;
import org.w2fc.geoportal.ws.exception.OutOfPermissionAreaException;

@Component
public class GeoportalSecurity {

    @Autowired
    ServiceRegistry serviceRegistry;

    public boolean isAdministrator(){
    	return Constants.isAdministrator();
    }
    
    public boolean isLayerEditor(Long layerId, Long parentLayerId) {
    	if (Constants.isAdministrator()) {
			return true;
		}

    	if(null == layerId)
    	    layerId = parentLayerId;

    	if(null == layerId)
    	    return false;

        GeoUser user = serviceRegistry.getUserDao().getCurrentGeoUser();
        List<GeoUser> users = serviceRegistry.getLayerDao().getAllowedUsersByLayerId(layerId);
        return users.contains(user);
    }
    
    
    public boolean isLayerEditor(Long layerId) {
        return isLayerEditor(layerId, null);
    }
    
    public boolean isLayerReader(Long layerId) {
    	if (Constants.isAdministrator()) {
			return true;
		}
    	if(null == layerId)
    	    return false;
    	GeoUser user = serviceRegistry.getUserDao().getCurrentGeoUser();
    	List<GeoUser> users = serviceRegistry.getLayerDao().getRelyUsersByLayerId(layerId);
    	return users.contains(user);
    }
    
    
    public boolean isObjectsAllowed(DeleteObjectsModel model){
    	return model.getObjIds().size() == serviceRegistry.getGeoObjectDao().countByLayerAndIds(model.getLayerId(), model.getObjIds());
    }
    
    @Transactional(readOnly=true)
    public boolean isObjectsAllowed(List<GeoObjectJSONModel> objects){
    	if (Constants.isEditor()) {
			return true;
		}
    	GeoUser user = serviceRegistry.getUserDao().getCurrentGeoUser();
    	List<GeoLayer> layers = serviceRegistry.getLayerDao().listTreeLayersEditable(user.getId());
    	int availables = 0;
    	for(GeoObjectJSONModel o : objects){
    		GeoObject obj = serviceRegistry.getGeoObjectDao().get(o.id);
    		for(GeoLayer l : obj.getGeoLayers()){
    			if(layers.contains(l)){
    				availables++;
    				break;
    			}
    		}
    	}
    	return availables == objects.size();
    }
    
    @Transactional(readOnly=true)
    public boolean isObjectAllowed(PortalObjectModel model){
    	if (Constants.isEditor()) {
			return true;
		}
    	GeoUser user = serviceRegistry.getUserDao().getCurrentGeoUser();
    	List<GeoLayer> layers = serviceRegistry.getLayerDao().listTreeLayersEditable(user.getId());
    	
    	GeoObject obj = serviceRegistry.getGeoObjectDao().get(model.getId());
    	for(GeoLayer l : obj.getGeoLayers()){
    		if(layers.contains(l)){
    			return true;
    		}
    	}
    	return false;
    }
    
    @Transactional(readOnly=true)
    public boolean isObjectAllowed(Long id) {
		return Constants.isEditor() || checkLayerPermissions(id);
	}

	@Transactional(readOnly=true)
	public boolean checkLayerPermissions(long id){
		GeoUser user = serviceRegistry.getUserDao().getCurrentGeoUser();
		List<GeoLayer> layers = serviceRegistry.getLayerDao().listTreeLayersEditable(user.getId());

		GeoObject obj = serviceRegistry.getGeoObjectDao().get(id);
		if (obj == null)
			throw new GeoObjectNotFoundException("Geo object with id #" + id + " does not exist");

		for(GeoLayer l : obj.getGeoLayers()){
			if(layers.contains(l)){
				return true;
			}
		}

		return false;
	}

	public void checkArea(GeoObject gisObject) {

		Long currentUserId = serviceRegistry.getUserDao().getCurrentGeoUser().getId();

		Map<String, Geometry> permissionAreaMap = serviceRegistry.getUserDao().getPermissionArea(currentUserId);

		if (permissionAreaMap == null)
			return;

		Geometry permArea = permissionAreaMap.get("area");

		if(permArea == null)
			throw new RuntimeException("Permission area not specified for current user");

		if(!permArea.contains(gisObject.getTheGeom()))throw new OutOfPermissionAreaException("The area is not available for editing");
	}

	public void checkExists(Long id){
		if (id == null)
			throw new GeoObjectNotFoundException("Geo object does not exist");

		GeoObject geoObject = serviceRegistry.getGeoObjectDao().get(id);
		if (geoObject == null)
			throw new GeoObjectNotFoundException("Geo object with id #" + id + " does not exist");
	}

	public boolean checkExists(String extSysId, String guid){
		Long id = serviceRegistry.getGeoObjectDao().getGeoObjectId(guid, extSysId);

		return id != null;
	}

	public void checkLayersPermissions(Set<Long> layerIds){
		for (Long layerId : layerIds) {
			if (!isLayerEditor(layerId))
				throw new AccessDeniedException("Current user has not access to layer "+layerId);
		}
	}
}
