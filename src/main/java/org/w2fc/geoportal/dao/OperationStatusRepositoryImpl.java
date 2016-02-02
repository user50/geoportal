package org.w2fc.geoportal.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.w2fc.geoportal.domain.OperationStatus;

/**
 * @author Yevhen
 */
public class OperationStatusRepositoryImpl implements OperationStatusRepository{

    private SessionFactory sessionFactory;

    public OperationStatusRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Long save(OperationStatus operationStatus) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Long id = null;
        try{
            tx = session.beginTransaction();
            id = (Long) session.save(operationStatus);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return id;
    }
}
