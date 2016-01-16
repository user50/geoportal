package org.w2fc.geoportal.dao;

import java.util.List;

import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoUser;


public interface GeoLayerDao extends AbstractDao<GeoLayer, Long> {
    
    public Long getObjectsCount(Long id);
    public List<GeoLayer> listRegistryServices();
    public List<GeoLayer> listRegistryLayers();
    public List<GeoLayer> listTreeLayers(Long userId);
    public List<GeoLayer> listTreeLayersEditable(Long userId);
	public List<GeoUser> getAllowedUsersByLayerId(Long id);
	public List<String> getAllTagKeysByLayer(Long id);
	public List<GeoLayer> listByUserId(Long id);
	public List<GeoUser> getRelyUsersByLayerId(Long layerId);
	public Long getObjectsCountByNameFilter(Long id, String nameFilter);
	public Long getObjectsLastRevision(Long layerId);
	public Long getObjectsLastDeleteRevision();
	public Integer getHaveObjects(Long id);
}
