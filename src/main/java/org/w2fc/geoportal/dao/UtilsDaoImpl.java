package org.w2fc.geoportal.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
@Service
public class UtilsDaoImpl implements UtilsDao{

    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private SessionFactory sessionFactoryCartography;
    
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Object> exec(String query) {
        return sessionFactory.getCurrentSession().createQuery(query).list();
    }


    @Override
    public List<?> execSql(String query, DataSources ds) {
        return getSession(ds).getCurrentSession().createSQLQuery(query).list();
    }


    public SessionFactory getSession(DataSources ds) {
        SessionFactory sf;
        switch (ds) {
        case PORTAL:
            sf = sessionFactory;
            break;

        case CARTOGRAPHY:
            sf = sessionFactoryCartography;
            break;

        default:
            sf = sessionFactory;
            break;
        }
        return sf;
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Object> exec(String query, DataSources ds) {
        return getSession(ds).getCurrentSession().createQuery(query).list();
    }
}
