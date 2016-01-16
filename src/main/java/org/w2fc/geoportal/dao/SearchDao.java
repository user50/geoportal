package org.w2fc.geoportal.dao;

import java.util.List;

public interface SearchDao {
	public void index() throws InterruptedException;
	public <T> List<T> search(String term, Class<T> clazz);
}
