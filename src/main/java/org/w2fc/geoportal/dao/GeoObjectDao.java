package org.w2fc.geoportal.dao;

import java.util.List;
import java.util.Map;

import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.wms.ParamsContainer;


public interface GeoObjectDao extends AbstractDao<GeoObject, Long> {
    
    public List<GeoObject> listByLayerId(Long identifier);

	public List<GeoObject> listByLayerIdPropertiesFetched(Long layerId);

	public GeoObject getPropertiesFetched(Long id);

	public List<GeoObject> getByFias(String fias);
    public List<String> getTagsByPartialName(String q);

	public List<GeoObject> listByLayerIdNotManaged(Long identifier);

	public List<GeoObject> listByLayerIdCached(Long identifier);

	public List<GeoUser> getAllowedUsersByObjectId(Long id);

    public Boolean saveBatch(List<GeoObject> list);

	public List<GeoObject> getByPointOrFias(Double lat, Double lng, String fias, List<Long> layersIds);

	public void addToLayer(Long objId, Long layerId) throws Exception;
	public void removeFromLayer(Long objId, Long layerId);

	public Integer countByLayerAndIds(Long layerId, List<Long> objIds);

	public List<Map<String, Object>> listByLayerIdWithTags(Long layerId, List<String> tag_key, Integer start, Integer count, String orderby, String nameFilter);

	public List<GeoObject> listByLayerIdWithTags (Long layerId);
	
	public List<Object[]> getHistoryList(Long id);

	public GeoObject getByRevision(Long id, Integer revId);

	public void moveObjectListToLayer(List<Long> objList, Long sourceLayerId, Long targetLayerId);

	public void cloneObjectListToLayer(List<Long> objIdList, Long targetLayerId, GeoUser currentGeoUser);
	
	public List<GeoObject> listByIds(List<Long> objIdList);

	public List<GeoObject> listByIdsNotManaged(List<Long> checked_objs);

	public void deleteObjectListFromLayer(Long currentLayerId, List<Long> checked_objs);

	public List<GeoObject> listLonelyObjectsByListId(List<Long> checked_objs);

	public List<GeoObject> listByLayerIdPartial(Long layerId, Long start, Long count);

	public List<GeoObject> listByLayerIdFromRevision(Long layerId, Long revId);

	public List<GeoObject> listByLayerIdAndTagKey(Long layerId, String key);

	public List<GeoObject> getByPointAndLayers(Double lat, Double lon, List<Long> layerIdList);

	public Integer getLastRevision(Long id);

	public Double getArea(Long id);

	List<GeoObject> listByLayerIdGeneralized(Long layerId, ParamsContainer params, Float g);

	public Long getGeoObjectId(String guid, String extSysId);

	public GeoObject getWithTags(Long id);

	public GeoObject mergeUpdate(GeoObject object, boolean... forceFlush);
}
