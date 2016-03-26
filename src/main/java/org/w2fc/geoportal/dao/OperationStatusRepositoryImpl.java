package org.w2fc.geoportal.dao;

import com.vividsolutions.jts.geom.Geometry;
import org.hibernate.*;
import org.w2fc.geoportal.domain.GeoObject;
import org.w2fc.geoportal.domain.OperationStatus;
import org.w2fc.geoportal.reports.ReportRaw;

import java.util.*;

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
    public List<OperationStatus> get(String pid) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        List<OperationStatus> operationStatusList = new ArrayList<OperationStatus>();
        try{
            tx = session.beginTransaction();
            Query query = session.createQuery("from OperationStatus where pid = :pid ");
            query.setParameter("pid", pid);
            operationStatusList = (List<OperationStatus>) query.list();
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }

        return operationStatusList;
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

    @Override
    public List<ReportRaw> reports() {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            String sql = "SELECT pid,  login,  min(date) as date\n" +
                    "from operation_status\n" +
                    "  LEFT JOIN geo_user on operation_status.user_id = geo_user.id\n" +
                    "GROUP BY pid, login\n" +
                    "ORDER BY date DESC";
            List<Object[]> rows = session.createSQLQuery(sql).list();
            if (rows != null && rows.size() > 0) {
                ReportRaw[] objects = new ReportRaw[rows.size()];
                for(int j = 0; j < rows.size() ; j++){
                    Object[] row = rows.get(j);
                    String pid = (String) row[0];
                    String login = (String) row[1];
                    Date date = (Date) row[2];
                    objects[j] = new ReportRaw(date, pid, login);
                }
                return Arrays.asList(objects);
            }
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return new ArrayList<ReportRaw>();
    }
}
