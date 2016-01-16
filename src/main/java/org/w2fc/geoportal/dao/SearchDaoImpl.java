package org.w2fc.geoportal.dao;

import java.util.List;

import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
@Service
public class SearchDaoImpl implements SearchDao {

	@Autowired
	private SessionFactory sessionFactory;
	

	/**
	 * Publication of the Hibernate Session
	 * 
	 * @return org.hibernate.Session - Hibernate Session
	 */
	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void index() throws InterruptedException {
		FullTextSession fullTextSession = Search.getFullTextSession(getCurrentSession());
		fullTextSession.createIndexer().start();
		//fullTextSession.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> search(String term, Class<T> clazz) {
		FullTextSession fullTextSession = Search.getFullTextSession(getCurrentSession());

		QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(clazz).get();
		Query luceneQuery = queryBuilder.keyword().onFields("name", "tags.key", "tags.value").matching(term).createQuery();

		// wrap Lucene query in a javax.persistence.Query
		org.hibernate.Query fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery, clazz);
		fullTextQuery.setMaxResults(100);
		List<T> geoObjList = fullTextQuery.list();

		//fullTextSession.close();

		return geoObjList;
	}

}
