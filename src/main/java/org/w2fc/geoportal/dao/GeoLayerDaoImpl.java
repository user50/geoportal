package org.w2fc.geoportal.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoLayerMetadata;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.utils.ServiceRegistry;


@Service
@Repository
public class GeoLayerDaoImpl extends AbstractDaoDefaulImpl<GeoLayer, Long> implements GeoLayerDao {

	@Autowired
	private ServiceRegistry serviceRegistry;
	
    protected GeoLayerDaoImpl() {
        super(GeoLayer.class);
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<GeoLayer> list() {
        return getCurrentSession()
                .createQuery("Select l from GeoLayer l left join fetch l.metadata m left join fetch m.changedBy chb left join fetch m.createdBy crb")
                .list();
    }


	@SuppressWarnings("unchecked")
	@Override
	public List<GeoLayer> listByUserId(Long id) {
		List<GeoLayer> listByUser = getCurrentSession()
				.createQuery("select l from GeoLayer l left join fetch l.metadata m join l.layerToUserReferences r join r.referenceId.geoUser u where u.id = :id")
				.setLong("id", id)
				.list();
		List<GeoLayer> listByRole = getCurrentSession()
		        .createQuery("select l from GeoLayer l left join fetch l.metadata m join l.layerToRoleReferences rr join rr.referenceId.geoUserRole.geoUsers u where u.id = :id")
		        .setLong("id", id)
		        .list();
		List<GeoLayer> res = new ArrayList<GeoLayer>();
		res.addAll(listByUser);
		res.addAll(listByRole);
		return res;
		
	}
    
    @Override
    public Long getObjectsCount(Long id) {
        return (Long) getCurrentSession()
                .createQuery("Select cast(count(o) as long) from GeoLayer l inner join l.geoObjects o where l.id = :id")
                .setLong("id", id)
                .uniqueResult();
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<GeoLayer> listRegistryServices() {
        return getCurrentSessionPortal().createQuery(
                "from GeoLayer p left join fetch p.metadata where p.typeId < 1 order by p.name asc")
                .list();
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<GeoLayer> listRegistryLayers() {
        return getCurrentSessionPortal().createQuery(
                "from GeoLayer p left join fetch p.metadata where p.typeId > 0 order by p.name asc")
                .list();
    }


    @SuppressWarnings({"unchecked", "serial", "rawtypes"})
    @Cacheable(value="layerPermanent", key="#userId")
    @Override
    public List<GeoLayer> listTreeLayers(Long userId) {
        String sql = getResourceSQL("GeoLayerDaoImpl.listTreeLayers");


        return getCurrentSession().createSQLQuery(sql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("name", StandardBasicTypes.STRING)
                .addScalar("parent_id", StandardBasicTypes.LONG)
                .addScalar("type_id", StandardBasicTypes.LONG)
                .addScalar("url", StandardBasicTypes.STRING)
                .addScalar("line_color", StandardBasicTypes.STRING)
                .addScalar("line_weight", StandardBasicTypes.INTEGER)
                .addScalar("fill_color", StandardBasicTypes.STRING)
                .addScalar("fill_opacity", StandardBasicTypes.INTEGER)
                .addScalar("tmpl_id", StandardBasicTypes.LONG)
                .addScalar("permissions", StandardBasicTypes.INTEGER)
                .addScalar("view_by_object", StandardBasicTypes.BOOLEAN)
                .setLong("id", userId)
                .setResultTransformer(new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {

                        GeoLayer l = new GeoLayer();

                        l.setId((Long) tuple[0]);
                        l.setName((String) tuple[1]);
                        l.setParentId((Long) tuple[2]);
                        l.setTypeId((Long) tuple[3]);
                        l.setUrl((String) tuple[4]);
                        l.setLineColor((String) tuple[5]);
                        l.setLineWeight((Integer) tuple[6]);
                        l.setFillColor((String) tuple[7]);
                        l.setFillOpacity((Integer) tuple[8]);
                        if(null != tuple[9]){
                            /*l.setPopupTemplate(new AddnsPopupTemplate());
                            l.getPopupTemplate().setId((Long) tuple[9]);*/
                        	l.setPopupTemplate(serviceRegistry.getPopupTemplateDao().get((Long) tuple[9]));
                        }

                        l.setPermissions((Integer) tuple[10]);
                        if(null != tuple[11]){
                        	GeoLayerMetadata metadata = new GeoLayerMetadata();
                        	metadata.setViewByObject((Boolean) tuple[11]);
                        	l.setMetadata(metadata);
                        }
                        return l;
                    }

                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                })
                .list();
    }


	@Override
    public List<GeoLayer> listTreeLayersEditable(Long userId) {
        List<GeoLayer> list = listTreeLayers(userId);
        
        Iterator<GeoLayer> iterator = list.iterator();
        
        while (iterator.hasNext()) {
            GeoLayer geoLayer = iterator.next();
            
            if(1 != geoLayer.getPermissions()){
                iterator.remove();
            }
        }
        
        return list;
    }


    @SuppressWarnings("unchecked")
	@Override
	public List<GeoUser> getAllowedUsersByLayerId(Long id) {
		String sql = getResourceSQL("GeoLayerDaoImpl.getAllowedUsersByLayerId");
		return getCurrentSession().createSQLQuery(sql).addEntity(GeoUser.class)
				.setLong("layerId", id).list();
		
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public List<GeoUser> getRelyUsersByLayerId(Long layerId) {
		String sql = getResourceSQL("GeoLayerDaoImpl.getRelyUsersByLayerId");
		return getCurrentSession().createSQLQuery(sql).addEntity(GeoUser.class)
				.setLong("layerId", layerId).list();
	}
    
    @Override
    @CacheEvict(value="layerPermanent", allEntries = true)
    public GeoLayer add(GeoLayer object, boolean... forceFlush) {
    	return super.add(object, forceFlush);
    }
    
    @Override
    @CacheEvict(value="layerPermanent", allEntries = true)
    public GeoLayer update(GeoLayer object, boolean... forceFlush) {
    	return super.update(object, forceFlush);
    }
    
    @Override
    @CacheEvict(value="layerPermanent", allEntries = true)
    public void remove(Long identifier, boolean... forceFlush) {
    	super.remove(identifier, forceFlush);
    }

    @SuppressWarnings("unchecked")
	@Override
	public List<String> getAllTagKeysByLayer(Long id) {
		String sql = getResourceSQL("GeoLayerDaoImpl.getAllTagsByLayer");
		return getCurrentSession().createSQLQuery(sql)
				.setLong("layerId", id).list();
	}


	@Override
	public Long getObjectsCountByNameFilter(Long id, String nameFilter) {
		 return (Long) getCurrentSession()
	                .createQuery("Select cast(count(o) as long) from GeoLayer l inner join l.geoObjects o where l.id = :id and o.name like :nameFilter")
	                .setLong("id", id)
	                .setString("nameFilter", "%" + nameFilter + "%")
	                .uniqueResult();
	}


	@Override
	public Long getObjectsLastRevision(Long layerId) {
		
		return ((Number) getCurrentSession()
				.createSQLQuery("select max(a.rev) from geo_object o inner join geo_layer_to_object lo on lo.object_id = o.id left join geo_object_aud a on a.id = o.id where lo.layer_id = :layerId")
				.setLong("layerId", layerId)
				.uniqueResult()).longValue();
	}


	@Override
	public Long getObjectsLastDeleteRevision() {
		return ((Number) getCurrentSession()
				.createSQLQuery("select max(a.rev) from geo_object_aud a where a.revtype = 2")
				.uniqueResult()).longValue();
	}


	@Override
	public Integer getHaveObjects(Long layerId) {
		return ((Number) getCurrentSession()
				.createSQLQuery(getResourceSQL("GeoLayerDaoImpl.getHaveObjects"))
				.setLong("layerId", layerId)
				.uniqueResult()).intValue();
	}


	@Override
	public Map<Long, Integer> getHaveObjects() {
		List<Map<Long,Integer>> countList = (List<Map<Long,Integer>>)getCurrentSession()
				.createSQLQuery(getResourceSQL("GeoLayerDaoImpl.getHaveObjects"))
				.addScalar("layer_id", StandardBasicTypes.LONG)
				.addScalar("count_obj", StandardBasicTypes.INTEGER)
				.setResultTransformer(new ResultTransformer() {

					private static final long serialVersionUID = -3236182483196688615L;

					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						Map<Long, Integer> l = new HashMap();
						l.put((Long) tuple[0], (Integer) tuple[1]);
						return l;
					}

					@Override
					public List transformList(List collection) {
						return collection;
					}
				})
				.list();
		Map<Long, Integer> res = new HashMap();
		for(Map<Long, Integer> m : countList){
			res.putAll(m);
		}
		return res;
	}


	@Override
	@Cacheable(value = "GeoPortalCache", key="'layer_'.concat(#layerId)")
	public GeoLayer getDetached(Long layerId) {
		
		GeoLayer l = get(layerId);
		getCurrentSession().evict(l);
		return l;
	}

    @Override
    public Set<GeoLayer> list(Set<Long> ids) {
        List<GeoLayer> layers = (List<GeoLayer>) getCurrentSession()
                .createQuery("Select l from GeoLayer l where l.id in (:ids)")
                .setParameterList("ids", ids)
                .list();

        return new HashSet<GeoLayer>(layers);
    }
}
