package org.w2fc.geoportal.dao;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.AuditQueryCreator;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.type.StandardBasicTypes;
import org.hibernatespatial.GeometryUserType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.wms.ParamsContainer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

@Service
@Repository
public class GeoObjectDaoImpl extends AbstractDaoDefaulImpl<GeoObject, Long> implements GeoObjectDao {

	private final static GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);//SRID
	
	protected GeoObjectDaoImpl() {
        super(GeoObject.class);
        
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GeoObject> listByLayerId(Long identifier) {
        return getCurrentSession()
                .createQuery("Select l.geoObjects from GeoLayer l where l.id = :id")
                .setLong("id", identifier)
                .list();
                
    }
    
    @SuppressWarnings("unchecked")
	public GeoObject getWithTags(Long id) {
		return (GeoObject) getCurrentSession()
				.createQuery("Select o from GeoObject o left join fetch o.tags where o.id = :id")
				.setLong("id", id)
				.uniqueResult();

	}
    
    @Cacheable(value="GeoPortalCache", key="#identifier")
    @Override
    public List<GeoObject> listByLayerIdCached(Long identifier) {
    	return listByLayerIdNotManaged(identifier);
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> listByIds(List<Long> objIdList) {
		return getCurrentSession()
                .createQuery("from GeoObject o where o.id in (:ids)")
                .setParameterList("ids", objIdList)
                .list();
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> listByIdsNotManaged(List<Long> objIdList) {
		 String sql = getResourceSQL("GeoObjectDao.listByIdsNotManaged");
			List<Object[]> rows = getCurrentSession().createSQLQuery(sql.toString()).
					addScalar("id", StandardBasicTypes.LONG).
					addScalar("name", StandardBasicTypes.STRING).
					addScalar("created_by", StandardBasicTypes.LONG).
					addScalar("changed_by", StandardBasicTypes.LONG).
					addScalar("created", StandardBasicTypes.DATE).
					addScalar("changed", StandardBasicTypes.DATE).
					addScalar("fias_code", StandardBasicTypes.STRING).
					addScalar("the_geom",  GeometryUserType.TYPE).
					addScalar("version", StandardBasicTypes.INTEGER).
					setParameterList("ids", objIdList).
		            list();
			if (rows != null && rows.size() > 0) {
				GeoObject[] objects = new GeoObject[rows.size()];
				for(int j = 0; j < rows.size() ; j++){
					GeoObject obj = new GeoObject();
					Object[] row = rows.get(j);
					obj.setId((Long)row[0]);
					obj.setVersion((Integer)row[8]);
					Geometry geom = (Geometry) row[7];
					obj.setTheGeom(geom);
	                obj.setName((String)row[1]);
	                obj.setChanged((Date)row[5]);
	                obj.setCreated((Date)row[4]);
	                obj.setFiasCode((String)row[6]);
	                objects[j] = obj;
				}
				return Arrays.asList(objects);
			}
			return new ArrayList<GeoObject>();
	}
    
    @SuppressWarnings("unchecked")
    @Override
    public List<GeoObject> listByLayerIdNotManaged(Long identifier) {
        String sql = getResourceSQL("GeoObjectDao.listByLayerIdNotManaged");
		List<Object[]> rows = getCurrentSession().createSQLQuery(sql.toString()).
				addScalar("id", StandardBasicTypes.LONG).
				addScalar("name", StandardBasicTypes.STRING).
				addScalar("created_by", StandardBasicTypes.LONG).
				addScalar("changed_by", StandardBasicTypes.LONG).
				addScalar("created", StandardBasicTypes.DATE).
				addScalar("changed", StandardBasicTypes.DATE).
				addScalar("fias_code", StandardBasicTypes.STRING).
				addScalar("the_geom",  GeometryUserType.TYPE).
				addScalar("version", StandardBasicTypes.INTEGER).
				setLong("id", identifier).
	            list();
		if (rows != null && rows.size() > 0) {
			GeoObject[] objects = new GeoObject[rows.size()];
			for(int j = 0; j < rows.size() ; j++){
				GeoObject obj = new GeoObject();
				Object[] row = rows.get(j);
				obj.setId((Long)row[0]);
				obj.setVersion((Integer)row[8]);
				Geometry geom = (Geometry) row[7];
				obj.setTheGeom(geom);
                obj.setName((String)row[1]);
                obj.setChanged((Date)row[5]);
                obj.setCreated((Date)row[4]);
                obj.setFiasCode((String)row[6]);
                objects[j] = obj;
			}
			return Arrays.asList(objects);
		}
		return new ArrayList<GeoObject>();
    }

    @SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> listByLayerIdPropertiesFetched(Long identifier) {
		return getCurrentSession()
                .createQuery("Select o from GeoLayer l inner join l.geoObjects o left join fetch o.geoObjectProperties p left join fetch o.createdBy cb left join fetch o.changedBy chb where l.id = :id")
                .setLong("id", identifier)
                .list();
	}

	@Override
	@Cacheable("objectPermanent")
	public GeoObject getPropertiesFetched(Long id) {
		return (GeoObject) getCurrentSession()
                .createQuery("Select o from GeoObject o left join fetch o.geoObjectProperties p left join fetch o.createdBy cb left join fetch o.changedBy chb where o.id = :id")
                .setLong("id", id)
                .uniqueResult();
	}
	
	  
	@Override
	@CacheEvict(value="objectPermanent", allEntries=true)
	public GeoObject mergeUpdate(GeoObject object, boolean... forceFlush)
	{
		Session s = getCurrentSession();
		s.merge(object);
		flushSession(s, forceFlush);
		return object;
	}
	
	@Override
	@CacheEvict(value="objectPermanent", allEntries=true)
	public GeoObject update(GeoObject object, boolean... forceFlush) {
		return super.update(object, forceFlush);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> getByFias(String fias) {
		return getCurrentSession()
                .createQuery("Select o from GeoObject o where o.fiasCode like :fias")
                .setString("fias", fias + "%")
                .list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getTagsByPartialName(String q) {
	    return getCurrentSession()
	            .createQuery("select distinct t.key from GeoObjectTag t where upper(t.key) like upper(:q) order by t.key")
	            .setParameter("q", q + "%")
	            .list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoUser> getAllowedUsersByObjectId(Long id) {
		String sql = getResourceSQL("GeoObjectDaoImpl.getAllowedUsersByObjectId");
		return getCurrentSession().createSQLQuery(sql).addEntity(GeoUser.class)
				.setLong("id", id).list();
		
	}

    @Override
    public Boolean saveBatch(List<GeoObject> list) {
        
        for (GeoObject geoObject : list) {
            add(geoObject);
        }
        
        return true;
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> getByPointOrFias(Double lat, Double lng, String fias, List<Long> layersIds) {
		return getCurrentSession()
                .createSQLQuery(getResourceSQL("GeoObjectDaoImpl.getByPoint"))
                .addEntity(GeoObject.class)
                .setDouble("lat", lat)
                .setDouble("lng", lng)
                .setString("fias", fias + "%")
                .setParameterList("layersIds", layersIds)
                .list();
	}

	@Override
	@CacheEvict(value="objectPermanent", allEntries=true)
	public void addToLayer(Long objId, Long layerId) throws Exception {
		Integer isRealated = (Integer)  getCurrentSession()
                .createSQLQuery("SELECT cast(count(*) as int) from geo_layer_to_object where layer_id = :layerId and object_id = :id")
                .setLong("layerId", layerId)
                .setLong("id", objId)
                .uniqueResult();		
		if(isRealated > 0)throw new Exception("Object is already related");
		getCurrentSession().createSQLQuery("INSERT INTO geo_layer_to_object (layer_id, object_id) VALUES(:lId, :oId)")
		.setLong("oId", objId)
		.setLong("lId", layerId)
		.executeUpdate();
		getCurrentSession().createSQLQuery("INSERT INTO geo_layer_to_object_aud (layer_id, object_id, rev, revtype) VALUES(:lId, :oId, :rev, 0)")
		.setLong("oId", objId)
		.setLong("lId", layerId)
		.setLong("rev", AuditReaderFactory.get(getCurrentSession()).getRevisionNumberForDate(Calendar.getInstance().getTime()).longValue())
		.executeUpdate();
		
	}

	@Override
	public Integer countByLayerAndIds(Long layerId, List<Long> objIds) {
		return (Integer)  getCurrentSession()
                .createSQLQuery(getResourceSQL("GeoObjectDaoImpl.countByLayerAndIds"))
                .setLong("layerId", layerId)
                .setParameterList("ids", objIds)
                .uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> listByLayerIdWithTags(Long layerId, final List<String> tag_key, Integer start, Integer count, String orderby, String nameFilter) {
		StringBuffer sql = new StringBuffer();
		sql.append("select obj.id, obj.name ");
		if(tag_key.size() > 0){
			sql.append(", ");
			for(int i = 0; i < tag_key.size() ; i++){
				sql.append("ot").append(i).append(".value as \"param").append(i).append("\"");
				if(tag_key.size() > i+1)sql.append(", ");
			}
		}
		sql.append(" from geo_layer_to_object lto inner join geo_object obj on obj.id = lto.object_id ");
		if(tag_key.size() > 0){
			for(int i = 0; i < tag_key.size() ; i++){
				sql.append("left join geo_object_tag ot").append(i).append(" on ot").append(i).append(".object_id = obj.id and ot").append(i).append(".key = '").append(tag_key.get(i)).append("' ");
			}
		}
		sql.append("where lto.layer_id = ").append(layerId);
		if(nameFilter != null){
			sql.append(" and obj.name like '%").append(nameFilter).append("%'");
		}
		sql.append(" order by obj.").append(orderby).append(" offset ").append(start);
		sql.append(" limit ").append(count);
		
		return getCurrentSession().createSQLQuery(sql.toString()).setResultTransformer(new BasicTransformerAdapter(){
			@Override
			public Object transformTuple(Object[] tuple, String[] aliases) {
				Map result = new HashMap(tuple.length);
				for ( int i=0; i<tuple.length; i++ ) {
					String alias = aliases[i];
					if(alias.indexOf("param") > -1){
						Integer inx = Integer.valueOf(alias.substring(5));
						alias = tag_key.get(inx);
					}
					if ( alias != null ) {
						result.put( alias, tuple[i] );
					}
				}
				return result;
			}
		}).list();
	}

	@Override
	public List<Object[]> getHistoryList(Long id) {
		AuditQueryCreator query = AuditReaderFactory.get(getCurrentSession()).createQuery();
		List<Object []> resultList  = query.forRevisionsOfEntity(GeoObject.class, false, false).add(AuditEntity.id().eq(id)).getResultList();
		Collections.reverse(resultList);
		return resultList;
	}

	@Override
	public GeoObject getByRevision(Long id, Integer revId) {
		AuditQuery query = AuditReaderFactory.get(getCurrentSession()).createQuery().forEntitiesAtRevision(GeoObject.class, revId);
		query.add(AuditEntity.id().eq(id));
		GeoObject object = (GeoObject) query.getSingleResult();
		
		AuditQuery queryTags = AuditReaderFactory.get(getCurrentSession()).createQuery().forEntitiesAtRevision(GeoObjectTag.class, revId);
		//queryTags.add(AuditEntity.property("geoObject").eq(id));
		queryTags.add(AuditEntity.property("geoObject").eq(object));
		List tags = queryTags.getResultList();
		
		object.setTags(new HashSet<GeoObjectTag>(tags));
		return object;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> listByLayerIdFromRevision(Long layerId, Long revId) {
		/*List<GeoObject> objs = new ArrayList<GeoObject>();
		String sql = getResourceSQL("GeoObjectDao.listByLayerIdFromRevision");
		List<Object[]> rows = getCurrentSession().createSQLQuery(sql.toString()).
				addScalar("id", StandardBasicTypes.LONG).
				addScalar("name", StandardBasicTypes.STRING).
				addScalar("created_by", StandardBasicTypes.LONG).
				addScalar("changed_by", StandardBasicTypes.LONG).
				addScalar("created", StandardBasicTypes.DATE).
				addScalar("changed", StandardBasicTypes.DATE).
				addScalar("fias_code", StandardBasicTypes.STRING).
				addScalar("the_geom",  GeometryUserType.TYPE).
				addScalar("version", StandardBasicTypes.INTEGER).
				setLong("layerId", layerId).
				setLong("revId", revId).
	            list();
		if (rows != null && rows.size() > 0) {
			GeoObject[] objects = new GeoObject[rows.size()];
			for(int j = 0; j < rows.size() ; j++){
				GeoObject obj = new GeoObject();
				Object[] row = rows.get(j);
				obj.setId((Long)row[0]);
				obj.setVersion((Integer)row[8]);
				Geometry geom = (Geometry) row[7];
				obj.setTheGeom(geom);
                obj.setName((String)row[1]);
                obj.setChanged((Date)row[5]);
                obj.setCreated((Date)row[4]);
                obj.setFiasCode((String)row[6]);
                objects[j] = obj;
			}
			objs = Arrays.asList(objects);
		}*/
		
		
		List<GeoObject> objs = (List<GeoObject>) getCurrentSession()
				.createSQLQuery(getResourceSQL("GeoObjectDao.listByLayerIdFromRevision"))
				.addEntity(GeoObject.class)
				.setLong("layerId", layerId)
				.setLong("revId", revId)
				.list();
		if(objs.size() == 0)return objs;
		final Map<Long, GeoObject> mappedObj = new HashMap<Long, GeoObject>(); 
		for(GeoObject obj : objs){
			if(!"DELETED".equals(obj.getName()))mappedObj.put(obj.getId(), obj);
			HashSet<GeoObjectTag> fetchedTags = new HashSet<GeoObjectTag>();
			obj.setTags(fetchedTags);
		}
		if(mappedObj.size() > 0)getCurrentSession().createSQLQuery("select object_id, key, value from geo_object_tag where object_id in :objs").setParameterList("objs", mappedObj.keySet()).
				setResultTransformer(new BasicTransformerAdapter(){
					@Override
					public Object transformTuple(Object[] tuple, String[] aliases) {
						GeoObjectTag result = new GeoObjectTag();
						for ( int i=0; i<tuple.length; i++ ) {
							String alias = aliases[i];
							if(alias.equals("key")){
								result.setKey(tuple[i].toString());
							}
							if(alias.equals("value")){
								result.setValue(String.valueOf(tuple[i]));
							}
							if(alias.equals("object_id")){
								result.setGeoObject(mappedObj.get(((Number)tuple[i]).longValue()));
								Set<GeoObjectTag> fetchedTags = mappedObj.get(((Number)tuple[i]).longValue()).getTags();
								fetchedTags.add(result);
							}							
						}
						return result;
					}
				}).
				
				list();
		
		return objs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> listByLayerIdWithTags(Long layerId) {
		List<GeoObject> objs = (List<GeoObject>) getCurrentSession()
				.createQuery(("Select o from GeoLayer l inner join l.geoObjects o left join fetch o.geoObjectProperties p left join fetch o.createdBy cb left join fetch o.changedBy chb where l.id = :layerId"))
				.setLong("layerId", layerId)
				.list();
		if(objs.size() == 0)return objs;
		List<GeoObjectTag> tags = getCurrentSession().createQuery("SELECT t from GeoObjectTag t join t.geoObject o join o.geoLayers l where l.id = :layerId").setLong("layerId", layerId).list();
		for(GeoObject obj : objs){
			HashSet<GeoObjectTag> fetchedTags = new HashSet<GeoObjectTag>();
			for(GeoObjectTag tag :tags){
				if(tag.getGeoObject().getId() == obj.getId()){
					fetchedTags.add(tag);
				}
			}
			obj.setTags(fetchedTags);
		}
		return objs;
	}

	
	
	@Override
	@CacheEvict(value="objectPermanent", allEntries=true)
	public void moveObjectListToLayer(List<Long> objList, Long sourceLayerId,
			Long targetLayerId) {
		Number rev = AuditReaderFactory.get(getCurrentSession()).getRevisionNumberForDate(Calendar.getInstance().getTime());
		//Чтобы не было дубликатов сначала удалим уже существующие связи с целевым слоем
		getCurrentSession().createSQLQuery("INSERT INTO geo_layer_to_object_aud (layer_id, object_id, rev, revtype) SELECT layer_id, object_id, :rev, 2 FROM geo_layer_to_object where layer_id = :layerId and object_id in (:objList)")
		.setLong("layerId", targetLayerId)
		.setParameterList("objList", objList)
		.setLong("rev", rev.longValue())
		.executeUpdate();
		getCurrentSession().createSQLQuery("DELETE FROM geo_layer_to_object where layer_id = :layerId and object_id in (:objList) ")
		.setLong("layerId", targetLayerId)
		.setParameterList("objList", objList)
		.executeUpdate();
		
		getCurrentSession().createSQLQuery("UPDATE geo_layer_to_object SET layer_id = :targetId where layer_id = :sourceId and object_id in (:objList) ")
		.setLong("targetId", targetLayerId)
		.setLong("sourceId", sourceLayerId)
		.setParameterList("objList", objList)
		.executeUpdate();
		getCurrentSession().createSQLQuery("INSERT INTO geo_layer_to_object_aud (layer_id, object_id, rev, revtype) SELECT layer_id, object_id, :rev, 1 FROM geo_layer_to_object where layer_id = :targetId and object_id in (:objList)")
		.setLong("targetId", targetLayerId)
		.setParameterList("objList", objList)
		.setLong("rev", rev.longValue())
		.executeUpdate();
		
		
	}

	@Override
	@CacheEvict(value="objectPermanent", allEntries=true)
	public void cloneObjectListToLayer(List<Long> objIdList, Long targetLayerId, GeoUser currentGeoUser) {
		List<GeoObject> originalObjects = listByIds(objIdList);
		for(GeoObject orig : originalObjects){
			GeoObject obj = orig.clone();
			obj.setId(null);
			HashSet<GeoObjectTag> objectTags = new HashSet<GeoObjectTag>();
			for(GeoObjectTag prop : orig.getTags()){
				GeoObjectTag tag = new GeoObjectTag();
    			tag.setKey(prop.getKey());
    			tag.setValue(prop.getValue());
    			tag.setGeoObject(obj);
    			objectTags.add(tag);
			}
			obj.setTags(objectTags);
			obj.setCreatedBy(currentGeoUser);
			obj.setCreated(Calendar.getInstance().getTime());
			getCurrentSession().createSQLQuery("INSERT INTO geo_layer_to_object (layer_id, object_id) VALUES(:lId, :oId)")
			.setLong("oId", add(obj).getId())
			.setLong("lId", targetLayerId)
			.executeUpdate();
			getCurrentSession().createSQLQuery("INSERT INTO geo_layer_to_object_aud (layer_id, object_id, rev, revtype) VALUES(:lId, :oId, :rev, 0)")
			.setLong("oId", add(obj).getId())
			.setLong("lId", targetLayerId)
			.setLong("rev", AuditReaderFactory.get(getCurrentSession()).getRevisionNumberForDate(Calendar.getInstance().getTime()).longValue())
			.executeUpdate();
		}
	}
	
	
	@Override
	@CacheEvict(value="objectPermanent", allEntries=true)
	public void deleteObjectListFromLayer(Long currentLayerId, List<Long> checked_objs) {
		Number rev = AuditReaderFactory.get(getCurrentSession()).getRevisionNumberForDate(Calendar.getInstance().getTime());
		getCurrentSession().createSQLQuery("INSERT INTO geo_layer_to_object_aud (layer_id, object_id, rev, revtype) SELECT layer_id, object_id, :rev, 2 FROM geo_layer_to_object where layer_id = :layerId and object_id in (:objList)")
		.setLong("layerId", currentLayerId)
		.setParameterList("objList", checked_objs)
		.setLong("rev", rev.longValue())
		.executeUpdate();
		
		getCurrentSession().createSQLQuery("DELETE FROM geo_layer_to_object where layer_id = :layerId and object_id in (:objList) ")
		.setLong("layerId", currentLayerId)
		.setParameterList("objList", checked_objs)
		.executeUpdate();
	}
	
	@Override
	@CacheEvict(value="objectPermanent", allEntries=true)
	public void removeFromLayer(Long objId, Long currentLayerId) {
		Number rev = AuditReaderFactory.get(getCurrentSession()).getRevisionNumberForDate(Calendar.getInstance().getTime());
		getCurrentSession().createSQLQuery("INSERT INTO geo_layer_to_object_aud (layer_id, object_id, rev, revtype) VALUES(:lId, :oId, :rev, 2)")
		.setLong("lId", currentLayerId)
		.setLong("oId", objId)
		.setLong("rev", rev.longValue())
		.executeUpdate();
		
		getCurrentSession().createSQLQuery("DELETE FROM geo_layer_to_object where layer_id = :layerId and object_id = :objId ")
		.setLong("layerId", currentLayerId)
		.setLong("objId", objId)
		.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> listLonelyObjectsByListId(List<Long> checked_objs) {
		return getCurrentSession().createQuery("Select o from GeoObject o left join o.geoLayers l where o.id in (:objIds) group by o having count(l)=0")
				.setParameterList("objIds", checked_objs)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> listByLayerIdPartial(Long layerId, Long start, Long count) {
		Query query = getCurrentSession().createQuery("select l.geoObjects from GeoLayer l where l.id = :layerId");
		query.setLong("layerId", layerId);
		if(count != null)query.setMaxResults(count.intValue());
		query.setFirstResult(start.intValue());		
				
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> listByLayerIdAndTagKey(Long layerId, String key) {
		Query query = getCurrentSession().createQuery("select o from GeoObject o join o.geoLayers l join o.tags t where l.id = :layerId and t.key = :tagKey");
		query.setLong("layerId", layerId);
		query.setString("tagKey", key);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeoObject> getByPointAndLayers(Double lat, Double lon, List<Long> layerIdList) {
		return getCurrentSession()
                .createSQLQuery(getResourceSQL("GeoObjectDaoImpl.getByPointAndLayers"))
                .addEntity(GeoObject.class)
                .setDouble("lat", lat)
                .setDouble("lng", lon)
                .setParameterList("layers", layerIdList)
                .list();
	}

	@Override
	public Integer getLastRevision(Long id) {
		return (Integer) getCurrentSession()
                .createSQLQuery("select cast(max(rev) as int) from geo_object_aud where id  = :id")
                .setLong("id", id)
                .uniqueResult();
	}

	@Override
	public Double getArea(Long id) {
		return (Double) getCurrentSession()
                .createSQLQuery("select ST_Area(CAST(the_geom AS geography)) from geo_object where id  = :id")
                .setLong("id", id)
                .uniqueResult();
	}

	@Override
	public List<GeoObject> listByLayerIdGeneralized(Long layerId, ParamsContainer params, Float generalization) {

		Coordinate[] bounds = new Coordinate[] {
				new Coordinate(params.maxx,params.maxy),
				new Coordinate(params.minx,params.maxy),
				new Coordinate(params.minx,params.miny),
				new Coordinate(params.maxx,params.miny),
				new Coordinate(params.maxx,params.maxy)
		};
		LinearRing contour = factory.createLinearRing(bounds);
		Polygon bbox = factory.createPolygon(contour, null  );
		
		String sql = getResourceSQL("GeoObjectDao.listByLayerIdGeneralized");
		List<Object[]> rows = (List<Object[]>)getCurrentSession()
				.createSQLQuery(sql)
				.addScalar("id", StandardBasicTypes.LONG)
				.addScalar("name", StandardBasicTypes.STRING)
				.addScalar("the_geom",  GeometryUserType.TYPE)
				.setLong("layerId", layerId)
				.setString("bbox", "SRID=4326;" + bbox.toText())
				.setFloat("genFact", generalization)
				.list();
		if (rows != null && !rows.isEmpty() /*rows.size() > 0*/) {
			GeoObject[] objects = new GeoObject[rows.size()];
			for(int j = 0; j < rows.size() ; j++){
				GeoObject obj = new GeoObject();
				Object[] row = rows.get(j);
				obj.setId((Long)row[0]);
				
				Geometry geom = (Geometry) row[2];
				obj.setTheGeom(geom);
				obj.setName((String)row[1]);
				objects[j] = obj;
			}
			return Arrays.asList(objects);
		}
		
		return new ArrayList<GeoObject>();		
	}

	

	@Override
	public Long getGeoObjectId(String guid, String extSysId) {
		BigInteger id = (BigInteger) getCurrentSession()
				.createSQLQuery("SELECT id from geo_object where guid = :guid and ext_sys_id = :extSysId")
				.setString("guid", guid)
				.setString("extSysId", extSysId)
				.uniqueResult();

		return id != null ? id.longValue() : null;
	}



}
