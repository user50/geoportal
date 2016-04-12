package org.w2fc.geoportal.layer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.conf.Constants;
import org.w2fc.conf.ObjectFactory;
import org.w2fc.geoportal.domain.GeoACL;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.domain.GeoSettings;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.gis.GeoObjectUIAdapter;
import org.w2fc.geoportal.integration.ru.infor.LayerByNameComp;
import org.w2fc.geoportal.user.CustomUserDetails;
import org.w2fc.geoportal.utils.ServiceRegistry;

import com.vividsolutions.jts.geom.Geometry;

@Controller
@RequestMapping(value = "/mass")
public class MassEditorController {
	

	final Logger logger = LoggerFactory.getLogger(MassEditorController.class);
	
	
	
	@Autowired
    ServiceRegistry serviceRegistry;
	
	@RequestMapping(value="/edit/{id}")
    @Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#id)")
    public String getLayerById(@PathVariable Long id, Model model, @RequestParam(required = false) List<String> tag_key, HttpServletRequest request,
    		@RequestParam(required = false) Integer pagingSelector,
    		@RequestParam(required = false) String sortSelector,
    		@RequestParam(required = false) String nameFilter
    		){
		if(pagingSelector == null)pagingSelector = 20;
		if(sortSelector == null)sortSelector = "id";
		MassEditorSessionModel sessionModel = new MassEditorSessionModel(request.getSession(), id);
		TreeSet<String> allTags = new TreeSet<String>(serviceRegistry.getLayerDao().getAllTagKeysByLayer(id));
		
		//adding new user-defined tags
		if(tag_key != null)allTags.addAll(tag_key);
		
		Long totalCount = serviceRegistry.getLayerDao().getObjectsCount(id);
		
		model.addAttribute("tagList", allTags);
		model.addAttribute("layerId", id);
		model.addAttribute(MassEditorSessionModel.CHECKED_TAGS, tag_key);
		if(nameFilter == null || "".equals(nameFilter)){
			model.addAttribute("objectCount", totalCount);
		}else{
			model.addAttribute("objectCount", serviceRegistry.getLayerDao().getObjectsCountByNameFilter(id, nameFilter));
		}
		
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("pagingSelector", pagingSelector);
		model.addAttribute("sortSelector", sortSelector);
		model.addAttribute(MassEditorSessionModel.NAME_FILTER, nameFilter);
		
		GeoLayer layer = serviceRegistry.getLayerDao().get(id);
		
		model.addAttribute("layerName", layer.getName());
		
		sessionModel.setTagKeys(tag_key);
		sessionModel.setNameFilter(nameFilter);
		
		if("GET".equals(request.getMethod())){
			sessionModel.setCheckedObjs(new ArrayList<Long>());
			//request.getSession().setAttribute(CHECKED_OBJS, new ArrayList<Long>());
		}
		
		List<Long> checkedObjs = sessionModel.getCheckedObjs();
		model.addAttribute("checkedObjCount",  checkedObjs.size());
		
		List<GeoLayer> list;
		if(Constants.isAdministrator()){
        	 list = serviceRegistry.getLayerDao().list();
        }else{
        	list = serviceRegistry.getLayerDao().listTreeLayersEditable(serviceRegistry.getUserDao().getCurrentGeoUser().getId());
        }
		Collections.sort(list, new LayerByNameComp());
		model.addAttribute("layers", list);
		return "markup/mass/edit";
	}
	
	@RequestMapping(value="/load", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerId)")
    public  @ResponseBody List<Map<String, Object>> listObjects(@RequestParam Long layerId, @RequestParam Integer start, @RequestParam Integer count, @RequestParam String sort, HttpServletRequest request){
		MassEditorSessionModel sessionModel = new MassEditorSessionModel(request.getSession(), layerId);
		
		List<String> tag_key = sessionModel.getTagKeys();
		List<Long> checked_objs = sessionModel.getCheckedObjs();
		
		String nameFilter = sessionModel.getNameFilter();
		
		List<Map<String, Object>> listObjects = serviceRegistry.getGeoObjectDao().listByLayerIdWithTags(layerId, tag_key, start, count, sort, nameFilter);
		
		List<GeoSettings> settings = serviceRegistry.getGeoSettingsDao().list();
		boolean permControl = false;
		for(GeoSettings setting : settings){
			if(setting.getKey().equals("MASS_PERMISSION_CONTROL")){
				permControl = Integer.parseInt(setting.getValue()) == 1;
			}
		}
		
		for(Map<String, Object> obj : listObjects){
			Long id = ((Number)obj.get("id")).longValue();
			if(checked_objs.contains(id)){
				obj.put("checked", true);
			}else{
				obj.put("checked", false);
			}
			if(permControl)	obj.put("__edited__",checkArea(id));
		}
		return listObjects;
		
	}
	
	@RequestMapping(value="/check", method = RequestMethod.GET)
    //@PreAuthorize("@geoportalSecurity.isLayerEditor(#layerId)")
    public @ResponseBody Integer checkObject(@RequestParam Long layerId, @RequestParam Long id, @RequestParam Boolean checked, HttpServletRequest request){
		MassEditorSessionModel sessionModel = new MassEditorSessionModel(request.getSession(), layerId);
		List<Long> checked_objs = sessionModel.getCheckedObjs();
		
		if(checked){
			if(!checked_objs.contains(id))checked_objs.add(id);
		}else{
			if(checked_objs.contains(id))checked_objs.remove(id);
		}
		
		sessionModel.setCheckedObjs(checked_objs);
		return checked_objs.size();
	}
	
	@RequestMapping(value="/checkall", method = RequestMethod.GET)
	@Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerId)")
    public @ResponseBody Integer checkAllObject(@RequestParam Long layerId, @RequestParam Boolean check, HttpServletRequest request){
		MassEditorSessionModel sessionModel = new MassEditorSessionModel(request.getSession(), layerId);
		
		List<Long> checked_objs = new ArrayList<Long>();
		if(check){
			GeoLayer layer = serviceRegistry.getLayerDao().get(layerId);
			for(GeoObject obj : layer.getGeoObjects()){
				checked_objs.add(obj.getId());
			}
		}
			
		sessionModel.setCheckedObjs(checked_objs);
		
		return checked_objs.size();
		
	}
	
	@RequestMapping(value="/update", method = RequestMethod.GET)
	@Transactional
    //@PreAuthorize("@geoportalSecurity.isLayerEditor(#layerId)")
    public @ResponseBody Boolean updateObject(@RequestParam Long id, @RequestParam String key, String value){
		GeoObject object = serviceRegistry.getGeoObjectDao().get(id);
		if(!checkArea(object))return false;
		if("name".equals(key)){
			object.setName(value);
			object.setChanged(Calendar.getInstance().getTime());
			object.setChangedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
			serviceRegistry.getGeoObjectDao().update(object);
			return true;
		}
		
		Set<GeoObjectTag> tags = object.getTags();
		for(GeoObjectTag tag : tags){
			if(tag.getKey().equals(key)){
				tag.setValue(value);
				object.setChanged(Calendar.getInstance().getTime());
				object.setChangedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
				serviceRegistry.getGeoObjectDao().update(object);
				return true;
			}
		}
		GeoObjectTag tag = ObjectFactory.createGeoObjectTag(object, key, value);
		tags.add(tag);
		object.setChanged(Calendar.getInstance().getTime());
		object.setChangedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
		serviceRegistry.getGeoObjectDao().update(object);
		return true;
	}
	
	@RequestMapping(value="/tie", method = RequestMethod.POST)
	@Transactional
	@CacheEvict(value="GeoPortalCache", key="#targetId")
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#targetId)")
    public @ResponseBody Boolean makeObjectsToLayerRelations(@RequestParam Long layerId, @RequestParam Long targetId, HttpServletRequest request){
		MassEditorSessionModel sessionModel = new MassEditorSessionModel(request.getSession(), layerId);
		List<Long> checked_objs = sessionModel.getCheckedObjs();
		
		List<GeoObject> objList =  serviceRegistry.getGeoObjectDao().listByIdsNotManaged(checked_objs);
		for(GeoObject gis : objList){
		
			if(!checkArea(gis))continue;
    		
			try{ 
				serviceRegistry.getGeoObjectDao().addToLayer(gis.getId(), targetId);
			}catch(Exception e){
				logger.debug(e.getMessage(), e.getCause());
			}
		}
		return true;
	}
	
	@RequestMapping(value="/move", method = RequestMethod.POST)
	@Transactional
	@CacheEvict(value="GeoPortalCache", key="#targetId")
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#targetId)")
    public @ResponseBody Boolean changeObjectsToLayerRelations(@RequestParam Long layerId, @RequestParam Long targetId, HttpServletRequest request){
		MassEditorSessionModel sessionModel = new MassEditorSessionModel(request.getSession(), layerId);
		List<Long> checked_objs = sessionModel.getCheckedObjs();
		
		List<Long> objIdList = new ArrayList<Long>();
		List<GeoObject> objList =  serviceRegistry.getGeoObjectDao().listByIdsNotManaged(checked_objs);
		for(GeoObject obj : objList){
			if(checkArea(obj)){
				objIdList.add(obj.getId());
				checked_objs.remove(obj.getId());
			}
		}
		if(objIdList.isEmpty())return true;
		serviceRegistry.getGeoObjectDao().moveObjectListToLayer(objIdList, layerId, targetId);
		sessionModel.setCheckedObjs(checked_objs);
		return true;
	}
	
	
	@RequestMapping(value="/clone", method = RequestMethod.POST)
	@Transactional
	@CacheEvict(value="GeoPortalCache", key="#targetId")
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#targetId)")
    public @ResponseBody Boolean cloneObjectsToLayer(@RequestParam Long layerId, @RequestParam Long targetId, HttpServletRequest request){
		MassEditorSessionModel sessionModel = new MassEditorSessionModel(request.getSession(), layerId);
		List<Long> checked_objs = sessionModel.getCheckedObjs();
		
		List<Long> objIdList = new ArrayList<Long>();
		List<GeoObject> objList =  serviceRegistry.getGeoObjectDao().listByIdsNotManaged(checked_objs);
		for(GeoObject obj : objList){
			if(checkArea(obj)){
				objIdList.add(obj.getId());
				checked_objs.remove(obj.getId());
			}
		}
		if(objIdList.isEmpty())return true;
		//serviceRegistry.getGeoObjectDao().moveObjectListToLayer(objIdList, layerId, targetId);
		serviceRegistry.getGeoObjectDao().cloneObjectListToLayer(objIdList, targetId, serviceRegistry.getUserDao().getCurrentGeoUser());
		sessionModel.setCheckedObjs(checked_objs);
		return true;
	}
	
	
	//Проверка доступности объектв согласно териториальным правам
	private boolean checkArea(GeoObject gisObject) {
    	Geometry permArea = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object user = auth.getPrincipal();
    	if(user instanceof CustomUserDetails){
    		if(((CustomUserDetails)user).getPermissionArea() == null)return true;
    		permArea = ((CustomUserDetails)user).getPermissionArea().get("area");
    		if(permArea == null)return true;
    		if(!permArea.contains(gisObject.getTheGeom()))return false;
    	}
    	return true;
	}
	
	
	private Boolean checkArea(Long id) {
		GeoObject object = serviceRegistry.getGeoObjectDao().get(id);
		return checkArea(object);
	}
	
	@RequestMapping(value="/history/{id}", method = RequestMethod.GET)
	@PreAuthorize("@geoportalSecurity.isObjectAllowed(#id)")
	@Transactional
    public String getObjectHistoryPage(@PathVariable Long id, Model model, @RequestParam(required=false) Integer revId){
		
		GeoObject object;
		if(revId == null){
			object = serviceRegistry.getGeoObjectDao().getPropertiesFetched(id);
			revId = serviceRegistry.getGeoObjectDao().getLastRevision(id);
		}else{
			object = serviceRegistry.getGeoObjectDao().getByRevision(id, revId);
		}
		GeoUser creator = object.getCreatedBy();
        if(creator != null){
            model.addAttribute("creatorName",  creator.getFullName());
        }else{
            model.addAttribute("creatorName",  "нет информации");
        }
        GeoUser editor = object.getChangedBy();
        if(editor != null){
            model.addAttribute("editorName",  editor.getFullName());
        }else{
            model.addAttribute("editorName",  "нет информации");
        }   
		List<Object[]> history = serviceRegistry.getGeoObjectDao().getHistoryList(id);
		
		model.addAttribute("portalObject", new GeoObjectUIAdapter(object));
		model.addAttribute("history", ObjectFactory.createHistoryModelAdapter(history));
		model.addAttribute("revId", revId);
		
		
		return "markup/mass/history";
	}
	
	@RequestMapping(value="/delete_tag", method = RequestMethod.GET)
    @Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerId)")
    public @ResponseBody Boolean deleteTag(@RequestParam Long layerId, @RequestParam String key, HttpServletRequest request){
		
		//GeoLayer layer = serviceRegistry.getLayerDao().get(layerId);
		List<GeoObject> objList = serviceRegistry.getGeoObjectDao().listByLayerIdAndTagKey(layerId, key);
		
		for(GeoObject obj : objList){
			Set<GeoObjectTag> tags = obj.getTags();
			Iterator<GeoObjectTag> iter = tags.iterator();
			while (iter.hasNext()) {
				GeoObjectTag currentTag = iter.next();
				if (key.equals(currentTag.getKey())) {
					iter.remove();
					obj.setChanged(Calendar.getInstance().getTime());
					obj.setChangedBy(serviceRegistry.getUserDao().getCurrentGeoUser());
					serviceRegistry.getGeoObjectDao().update(obj);
					break;
				} 
			}
		}
		return true;
	}
	
	
	@RequestMapping(value="/delete", method = RequestMethod.GET)
    @Transactional
    @PreAuthorize("@geoportalSecurity.isLayerEditor(#layerId)")
    public @ResponseBody Integer deleteObjects(@RequestParam Long layerId, HttpServletRequest request){
		MassEditorSessionModel sessionModel = new MassEditorSessionModel(request.getSession(), layerId);
		List<GeoObject> aclObjs = serviceRegistry.getACLDao().listAllUsedObjects();
		
		List<Long> checked_objs = sessionModel.getCheckedObjs();
		
		//сначала разберемся на какие объекты у нас есть права
		List<Long> objIdList = new ArrayList<Long>();
		List<GeoObject> objList =  serviceRegistry.getGeoObjectDao().listByIdsNotManaged(checked_objs);
		for(GeoObject obj : objList){
			if(checkArea(obj)){
				objIdList.add(obj.getId());
				checked_objs.remove(obj.getId());
			}
		}
		
		//теперь отвяжем от слоя - это быстро
		serviceRegistry.getGeoObjectDao().deleteObjectListFromLayer(layerId, objIdList);
		
		//теперь получим объекты, которые после отвязки остались непривязаны ни к чему - их надо будет удалить
		List<GeoObject> objects = serviceRegistry.getGeoObjectDao().listLonelyObjectsByListId(objIdList);
		
		for(GeoObject obj : objects){
			if(aclObjs.contains(obj)){
				GeoACL acl = serviceRegistry.getACLDao().getByObjectId(obj.getId());
				serviceRegistry.getACLDao().remove(acl.getId());
			}
			/*obj.setName("DELETED");
			serviceRegistry.getGeoObjectDao().update(obj);*/
			serviceRegistry.getGeoObjectDao().remove(obj.getId());
		}
		
		sessionModel.setCheckedObjs(checked_objs);
		
		return objIdList.size();
	}
	
	
}
