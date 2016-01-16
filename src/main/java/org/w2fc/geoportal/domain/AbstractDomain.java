package org.w2fc.geoportal.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractDomain<T> {
    
    private static final String FIELD_NAME_ID = "id";
    
    private static final String FIELD_NAME_VERSION = "version";


    final Logger logger = LoggerFactory.getLogger(AbstractDomain.class);

    
    @SuppressWarnings("unchecked")
    public T clone(){
        T o = null;
        try {
            o = (T) BeanUtils.cloneBean(this);
            Field[] fieldList = o.getClass().getDeclaredFields();

           for (int i = 0; i < fieldList.length; i++) {
               if(necessaryToSkip(fieldList[i])){
                   PropertyUtils.setProperty(o, fieldList[i].getName(), null);
               }
           }
        } catch (Exception e) {
            logger.error("AbstractDomain:clone() - Something has gone wrong", e);
        }
        return o;
    }
    
    
    public void copyValuesFrom(T o, String ... skipFieldNames){
        
        List<String> excludeList = getExcludeList(this);
        excludeList.add(FIELD_NAME_ID);
        excludeList.add(FIELD_NAME_VERSION);
        excludeList.addAll(Arrays.asList(skipFieldNames));
        
        
        try {
            Field[] fieldList = this.getClass().getDeclaredFields();

            for (int i = 0; i < fieldList.length; i++) {
                if (!excludeList.contains(fieldList[i].getName())) {
                    PropertyUtils.setProperty(this, fieldList[i].getName(), PropertyUtils.getProperty(o, fieldList[i].getName()));
                }
            }
        } catch (Exception e) {
            logger.error("AbstractDomain:copyValuesFrom() - Something has gone wrong", e);
        }
    }
    
    
    public String toString(){
        try {
            // return BeanUtils.describe(this).toString();
            List<String> exclude = getExcludeList(this);
            
            ReflectionToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
            return ReflectionToStringBuilder.toStringExclude(this, exclude);
            
        } catch (Exception e) {
            logger.info("AbstractDomain:toString() - Something has gone wrong: " + e.getMessage());
        }
        return super.toString(); 
    }


    public static List<String> getExcludeList(Object o, boolean ... skipSomeFields) {
        List<String> exclude = new ArrayList<String>();
        exclude.add("logger");
        
        Field[] fieldList = getEntityClass(o).getDeclaredFields();

        for (int i = 0; i < fieldList.length; i++) {
            if(necessaryToSkip(fieldList[i], skipSomeFields)){
                exclude.add(fieldList[i].getName());
            }
        }
        return exclude;
    }


    private static boolean necessaryToSkip(Field field, boolean ... skipSomeFields) {
        return field.isAnnotationPresent(OneToMany.class) 
                || field.isAnnotationPresent(ManyToMany.class)
                || field.isAnnotationPresent(ManyToOne.class)
                || field.isAnnotationPresent(OneToOne.class)
                || field.isAnnotationPresent(Transient.class)
                || (0 == skipSomeFields.length && field.isAnnotationPresent(EmbeddedId.class));
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if(null == obj)
            return false;
        
        // check type of objects
        if (!checkType(this, obj))
            return false;
        
        // check references
        if (obj == this)
            return true;
        
        // check fields content
        EqualsBuilder eb = new EqualsBuilder();
        
        List<String> excludeList = getExcludeList(this, true);

        try {
            // check, if id is NULL then throw new Exception to logs 
            if(null == PropertyUtils.getProperty(this, FIELD_NAME_ID) && null == PropertyUtils.getProperty(obj, FIELD_NAME_ID)){
                logger.info("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"
                        + "AbstractDomain:equals() - Attempt of comparison TWO NEW objects. \n"
                        + "Entity class: " + this.getClass() + " \n"
                        + "Reference THIS: " + this +  " \n"
                        + "Reference OBJ:  " + obj +  " \n"
                        + "Fields '" + FIELD_NAME_ID +"' and '" +FIELD_NAME_VERSION + "' will remove from comparison \n"
                        + "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
                
            }else if(null == PropertyUtils.getProperty(this, FIELD_NAME_ID) || null == PropertyUtils.getProperty(obj, FIELD_NAME_ID)){
                excludeList.add(FIELD_NAME_ID);
                excludeList.add(FIELD_NAME_VERSION);
                
                try{
                    logger.error("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"
                            + "AbstractDomain:equals() - Attempt of comparison NEW and PERSISTED objects. \n"
                            + "Entity class: " + this.getClass() + " \n"
                            + "Reference THIS: " + this +  " \n"
                            + "Reference OBJ:  " + obj +  " \n"
                            + "Fields '" + FIELD_NAME_ID +"' and '" +FIELD_NAME_VERSION + "' will remove from comparison \n"
                            + "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n", new Exception());
                }catch(Exception e){}
            }
        
            Field[] fieldList = getEntityClass(this).getDeclaredFields();

            for (int i = 0; i < fieldList.length; i++) {
                if (!excludeList.contains(fieldList[i].getName())) {
                    eb.append(
                        PropertyUtils.getProperty(this, fieldList[i].getName()),
                        PropertyUtils.getProperty(obj, fieldList[i].getName())
                    );
                }
            }
        } catch (Exception e) {
            logger.error("AbstractDomain:equals() - Something has gone wrong", e);
        }
        
        return eb.isEquals();
    }
    
    
    @Override
    public int hashCode() {
        // add the class of entity to hashcode calculation
        HashCodeBuilder hb = new HashCodeBuilder(5, 27)
                                            .appendSuper(getEntityClass(this).hashCode());
        
        // add fields content
        List<String> excludeList = getExcludeList(this);
        excludeList.add(FIELD_NAME_ID);
        excludeList.add(FIELD_NAME_VERSION);
        
        try {
            Field[] fieldList = getEntityClass(this).getDeclaredFields();

            for (int i = 0; i < fieldList.length; i++) {
                if (!excludeList.contains(fieldList[i].getName())) {
                    hb.append(PropertyUtils.getProperty(this, fieldList[i].getName()));
                }
            }
        } catch (Exception e) {
            logger.error("AbstractDomain:hashCode() - Something has gone wrong", e);
        }
        
        return hb.hashCode();
    }


    private static boolean checkType(Object o1, Object o2) {
        Class<?> clazz = getEntityClass(o1);
        return clazz.isAssignableFrom(o2.getClass());
    }

    
    @SuppressWarnings({"unchecked"})
    private static Class<? super Object> getEntityClass(Object o1) {
        Class<?> clazz = o1.getClass();
        while (!Object.class.equals(clazz)) {
            if (clazz.getSuperclass().equals(AbstractDomain.class) 
                    && clazz.isAnnotationPresent(Entity.class))
                break;

            clazz = clazz.getSuperclass();
        }
        
        return (Class<? super Object>) clazz;
    }
}
