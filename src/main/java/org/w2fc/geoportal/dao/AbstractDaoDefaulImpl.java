package org.w2fc.geoportal.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;



import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;


@Transactional
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public abstract class AbstractDaoDefaulImpl<T, I extends Serializable> implements AbstractDao<T, I> {

    final Logger logger = LoggerFactory.getLogger(AbstractDaoDefaulImpl.class);
    
    private static enum DataSource{
        PORTAL, CARTOGRAPHY
    }
    
    private final Class genericType;
    
    private final DataSource ds;
    
    private final String findAllHql;
    
    
    @Autowired
    SessionFactory sessionFactory;
    
    @Autowired
    SessionFactory sessionFactoryCartography;




    /**
     * Define type (set up the concrete Java Class) DAO
     * 
     * @param genericType
     */
    public AbstractDaoDefaulImpl(Class<T> genericType){
        this.genericType = genericType;
        this.findAllHql = "from " + genericType.getName();

        if(this.genericType.getPackage().getName().startsWith("ru.infor.ws")){
            this.ds = DataSource.CARTOGRAPHY;

        }else{
            this.ds = DataSource.PORTAL;
        }
    }
    

    /**
     * Publication of the Hibernate Session
     * 
     * @return org.hibernate.Session - Hibernate Session
     */
    protected Session getCurrentSessionPortal() {
        return sessionFactory.getCurrentSession();
    }
    
    
    protected Session getCurrentSessionCartography() {
        return sessionFactoryCartography.getCurrentSession();
    }
  
    
    protected Session getCurrentSession() {
        switch (ds) {
            case PORTAL:
                return getCurrentSessionPortal();
    
            case CARTOGRAPHY:
                return getCurrentSessionCartography();
    
            default:
                return getCurrentSessionPortal();
        }
    }
    
    
    @Override
    public T add(T object, boolean ... forceFlush) {
        Session s = getCurrentSession();
        s.saveOrUpdate(object);
        flushSession(s, forceFlush);
        return object;
    }
    
    
    @Transactional(readOnly=true)
    @Override
    public List<T> list() {
        return getCurrentSession().createQuery(findAllHql).list();
    }

    
    @Transactional(readOnly=true)
    @Override
    public T get(I identifier) {
        return (T) getCurrentSession().get(genericType, identifier);
    }

    
    @Transactional(readOnly = true)
    @Override
    public T get(T object) {

        I identifier = null;
        
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(javax.persistence.Id.class)) {
                try {
                    identifier = (I) PropertyUtils.getProperty(object, field.getName());
                    break;
                    
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
        }

        return (T) getCurrentSession().get(genericType, identifier);
    }
    
    
    @Override
    public void remove(I identifier, boolean ... forceFlush) {
        Session s = getCurrentSession();
        s.delete(get(identifier));
        flushSession(s, forceFlush);
    }

    
    @Override
    public T update(T object, boolean ... forceFlush) {
        return add(object, forceFlush);
    }
    
    /**
     * 
     * @param s - HIBERNATE session
     * @param forceFlush - necessary to flush HIBERNATE session
     */
    protected void flushSession(Session s, boolean[] forceFlush){
        if(0 < forceFlush.length && forceFlush[0]) 
            s.flush();
    }
    
    @Override
    public String getResourceSQL(String name){
		InputStream str = null;
		try{
	        str = GeoObjectDaoImpl.class.getResourceAsStream("/"+name+".sql");
	        return IOUtils.toString(str);
	    }catch(IOException e){
	        throw new IllegalStateException("Failed to read SQL query", e);
	    }finally{
	        if(str != null)IOUtils.closeQuietly(str);
	    }
	}
    
}
