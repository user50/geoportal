package org.w2fc.geoportal.integration.ru.infor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.StandardBasicTypes;
import org.hibernatespatial.GeometryUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.w2fc.geoportal.dao.AbstractDaoDefaulImpl;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.GeoObjectTag;
import org.w2fc.geoportal.domain.GeoUser;
import org.w2fc.geoportal.user.CustomUserDetails;
import org.w2fc.geoportal.utils.ServiceRegistry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.Geometry;


@Service
@Repository
public class MigrationDaoImpl extends AbstractDaoDefaulImpl<GeoObject, Long> implements MigrationDao {
    
    final static Logger logger = LoggerFactory.getLogger(MigrationDaoImpl.class);
    
    @Autowired
    private ServiceRegistry serviceRegistry;
    
    @Autowired
    private PlatformTransactionManager txManager;

    
    protected MigrationDaoImpl() {
        super(GeoObject.class);
    }

    
    @SuppressWarnings({"unchecked", "serial"})
    @Override
    public List<GeoObject> getObjectsByTags(List<String> tags) {

        if(2 > tags.size()){
            return Collections.EMPTY_LIST;
        }

        // first tag for SQL performance
        String key0 = tags.get(0);
        String value0 = tags.get(1);

        
        // create SQL
        String sql = getResourceSQL("MigrationDaoImpl.getObjectsByTags");

        // add another tags
        StringBuilder sb = new StringBuilder();
        for (int i1 = 0; i1 < tags.size(); i1 = i1 + 2) {
            sb.append((0 != i1 ? "                      or ":"") + "(t3.key = '"+ tags.get(i1) +"'"
                    + ("".equals(tags.get(i1 + 1)) ? "" : " and t3.value = '"+ tags.get(i1 + 1) +"'")+") \n");
        }
        
        if(0 < sb.length())
            sql = 
                sql
                .replaceAll("true /\\* HOLE \\*/", sb.toString())
                .replaceAll("1 /\\* HOLE \\*/", String.valueOf(tags.size()/2));
                
        
        if("".equals(value0))
            sql = sql.replaceAll("and t.value = :value0", "/* DELETED */");


        final GeoObject[] ref = new GeoObject[1];

        Query query = getCurrentSessionCartography().createSQLQuery(sql)
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("prefix", StandardBasicTypes.STRING)
                .addScalar("name", StandardBasicTypes.STRING)
                .addScalar("the_geom_type", StandardBasicTypes.STRING)
                .addScalar("key", StandardBasicTypes.STRING)
                .addScalar("value", StandardBasicTypes.STRING)

                .setString("key0", key0);
                
        if(! "".equals(value0))
            query.setString("value0", value0);
        
        
        return query.setResultTransformer(new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        
                        // get all fields 
                        Long id = (Long) tuple[0];
                        final String prefix = (String) tuple[1];
                        String name = (String) tuple[2];
                        final String theGeomType = (String) tuple[3];
                        String key = (String) tuple[4];
                        String value = (String) tuple[5];
                        
                        // create the new tag
                        GeoObjectTag t = new GeoObjectTag();
                        t.setKey(key);
                        t.setValue(value);

                        if(null == ref[0] || !ref[0].getId().equals(id)){
                            
                            // create the new GeoObject and add some JSON fields 
                            ref[0] = new GeoObject(){
                                @JsonProperty @Override
                                public java.util.Set<GeoObjectTag> getTags() { return super.getTags(); }

                                @JsonProperty
                                public String getPrefix() { return prefix; }
                                
                                @JsonProperty
                                public String getGeomType() { return theGeomType; }
                            };

                            ref[0].setId(id);
                            ref[0].setName(name);

                            ref[0].setTags(new HashSet<GeoObjectTag>());
                            ref[0].getTags().add(t);

                            // return GeoObject
                            return ref[0];
                        }

                        // if it the same GeoObject return nothing, just add new tag
                        ref[0].getTags().add(t);
                        return null;
                    }


                    @SuppressWarnings("rawtypes")
                    @Override
                    public List transformList(List collection) {
                        // remove all nulls
                        collection.removeAll(Collections.singleton(null));
                        return collection;
                    }
                })
                .list();
    }

    
    @Override
    public Boolean importByIds(Long layerId, List<String> objIds) {

        List<Long> nodeIds = new ArrayList<Long>();
        List<Long> waysIds = new ArrayList<Long>();

        for (String str : objIds) {
            if(str.startsWith("n")){
                nodeIds.add(Long.parseLong(str.substring(1)));
            }else{
                waysIds.add(Long.parseLong(str.substring(1)));
            }
        }

        String sql = getResourceSQL("MigrationDaoImpl.importByIds.Nodes");
        List<GeoObject> list1 =  execSqlWithIds(nodeIds, sql);

        sql = getResourceSQL("MigrationDaoImpl.importByIds.Ways");
        List<GeoObject> list2 = execSqlWithIds(waysIds, sql);

        list1.addAll(list2);

        if(0 == list1.size())
            return true;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object user = auth.getPrincipal();
        Geometry permArea = null;
        if(user instanceof CustomUserDetails){
            if(((CustomUserDetails)user).getPermissionArea() != null){
                permArea = ((CustomUserDetails)user).getPermissionArea().get("area");
            }else{
                permArea = null;
            }
        }else{
            permArea = null;
        }

        // if there is what we can save then open transaction
        TransactionStatus tx = txManager.getTransaction(new DefaultTransactionDefinition());
        Boolean saveBatchRes = false;
        try{
            GeoLayer l = serviceRegistry.getLayerDao().get(layerId);
            GeoUser u = serviceRegistry.getUserDao().getCurrentGeoUser();

            Iterator<GeoObject> iterator = list1.iterator();
            while (iterator.hasNext()) {
                
                GeoObject geoObject = iterator.next();
                
                if(!checkArea(permArea, geoObject)){
                    iterator.remove();
                    continue;
                }
                
                geoObject.setGeoLayers(new HashSet<GeoLayer>());

                geoObject.getGeoLayers().add(l);
                l.getGeoObjects().add(geoObject);

                geoObject.setCreatedBy(u);
                geoObject.setCreated(new Date());
            }

            saveBatchRes = serviceRegistry.getGeoObjectDao().saveBatch(list1);
            txManager.commit(tx);
            
        }catch(Exception e){
            logger.error(e.getLocalizedMessage(), e);
            txManager.rollback(tx);    
        }

        return saveBatchRes;
    }

    
    private boolean checkArea(Geometry permArea, GeoObject gisObject) {
        if(permArea == null)return true;
        if(permArea.contains(gisObject.getTheGeom())){
            return true;
        }else{
            logger.warn("Ошибка! Территория не доступна для редактирования.");
            return false;
        }
    }
    

    @SuppressWarnings({"unchecked", "serial"})
    private List<GeoObject> execSqlWithIds(List<Long> ids, String sql) {
        
        if(0 == ids.size()){
            return new LinkedList<GeoObject>();
        }

        final GeoObject[] ref = new GeoObject[1];
        final Long[] refId = new Long[1];

        return getCurrentSessionCartography().createSQLQuery(sql)

                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("name", StandardBasicTypes.STRING)
                .addScalar("the_geom",  GeometryUserType.TYPE)
                .addScalar("tag_id", StandardBasicTypes.LONG)
                .addScalar("key", StandardBasicTypes.STRING)
                .addScalar("value", StandardBasicTypes.STRING)

                .setParameterList("ids", ids)

                .setResultTransformer(new ResultTransformer() {

                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        // get all fields 
                        Long id = (Long) tuple[0];
                        String name = (String) tuple[1];
                        Geometry theGeom = (Geometry) tuple[2];
                        // skip tuple[3] this is tag_id
                        String key = (String) tuple[4];
                        String value = (String) tuple[5];

                        // create the new tag
                        GeoObjectTag t = new GeoObjectTag();
                        t.setKey(key);
                        t.setValue(value);
                        
                        if(null == refId[0] || !refId[0].equals(id)){
                            refId[0] = id;
                            
                            // create the new GeoObject 
                            ref[0] = new GeoObject();
                            ref[0].setName(null == name ? "" : name);
                            ref[0].setTheGeom(theGeom);

                            ref[0].setTags(new HashSet<GeoObjectTag>());
                            ref[0].getTags().add(t);
                            t.setGeoObject(ref[0]);
                            
                            GeoObjectTag t1 = new GeoObjectTag();
                            t1.setKey("externalId");
                            t1.setValue(null == id ? "" : id.toString());
                            ref[0].getTags().add(t1);
                            t1.setGeoObject(ref[0]);

                            // return GeoObject
                            return ref[0];
                        }

                        // if it the same GeoObject return nothing, just add new tag
                        ref[0].getTags().add(t);
                        t.setGeoObject(ref[0]);
                        return null;
                    }

                    @SuppressWarnings("rawtypes")
                    @Override
                    public List transformList(List collection) {
                        // remove all nulls
                        collection.removeAll(Collections.singleton(null));
                        return collection;
                    }
                })
                .list();
    }
}
