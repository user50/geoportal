package org.w2fc.geoportal.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.w2fc.geoportal.domain.OperationStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    @Override
    public List<OperationStatus> list() {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        List<OperationStatus> operationStatusList = new ArrayList<OperationStatus>();
        try{
            tx = session.beginTransaction();
            operationStatusList = (List<OperationStatus>) session.createQuery("FROM OperationStatus").list();
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }

        return operationStatusList;
    }
}
