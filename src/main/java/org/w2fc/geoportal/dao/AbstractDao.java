package org.w2fc.geoportal.dao;

import java.io.Serializable;
import java.util.List;


public interface AbstractDao <T, I extends Serializable>    {
    
    public T add(T object, boolean ... forceFlush);

    public List<T> list();
    
    public T get(I identifier);
    
    public T get(T object);

    public void remove(I identifier, boolean ... forceFlush);
    
    public T update(T object, boolean ... forceFlush);

	String getResourceSQL(String name);
}
