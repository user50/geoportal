package org.w2fc.geoportal.fias;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.spell.LevensteinDistance;
import org.hibernate.SessionFactory;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Repository
public class FiasDaoImpl {

    final Logger logger = LoggerFactory.getLogger(FiasController.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional(readOnly=true)
    public List<List<AddressObject>> search(String q){

        @SuppressWarnings("unchecked")
        List<Object[]> sqlRes = sessionFactory.getCurrentSession()
        .createSQLQuery(
                "WITH RECURSIVE child_to_parents AS ("
                        + " SELECT row_number() over() as rownum,  a.* FROM addrobj2 a "
                        + " WHERE aoid IN (SELECT aoid FROM addrobj2 WHERE UPPER(formalname) LIKE UPPER(:q)) "
                        + " UNION ALL "
                        + " SELECT c.rownum,  b.* FROM addrobj2 b, child_to_parents c WHERE b.aoguid = c.parentguid AND b.currstatus = 0 "
                        + " ) SELECT rownum, aolevel, shortname, formalname, code FROM child_to_parents ORDER BY rownum, aolevel;")
                        .addScalar("rownum", new IntegerType())
                        .addScalar("aolevel", new IntegerType())
                        .addScalar("shortname", new StringType())
                        .addScalar("formalname", new StringType())
                        .addScalar("code", new StringType())
                        .setString("q", q +"%")
                        .list();

        List<List<AddressObject>> result = new ArrayList<List<AddressObject>>();
        List<AddressObject> r1 = new ArrayList<FiasDaoImpl.AddressObject>();
        int rownum = 0;

        for (Object[] row : sqlRes) {

            if((Integer) row[0] != rownum){
                rownum = (Integer) row[0];
                r1 = new ArrayList<FiasDaoImpl.AddressObject>();
                result.add(r1);
            }

            AddressObject o = new AddressObject((String) row[2], (String)row[3], (Integer)row[1], (String)row[4]);
            r1.add(o);
        }

        return result;
    }

    @Transactional(readOnly=true)
    public List<AddressObject> search(String q, int levelLow,  int levelHigh){

        @SuppressWarnings("unchecked")
        List<Object[]> sqlRes = sessionFactory.getCurrentSession()
        .createSQLQuery(
                " SELECT distinct aolevel, shortname, formalname, code FROM addrobj2 "
                        + " WHERE aolevel >= :levelLow AND aolevel <= :levelHigh AND UPPER(formalname) LIKE UPPER(:q) "
                        + " ORDER BY formalname")
                        .addScalar("aolevel", new IntegerType())
                        .addScalar("shortname", new StringType())
                        .addScalar("formalname", new StringType())
                        .addScalar("code", new StringType())
                        .setString("q", q +"%")
                        .setInteger("levelLow", levelLow)
                        .setInteger("levelHigh", levelHigh)
                        .list();

        List<AddressObject> result = new ArrayList<FiasDaoImpl.AddressObject>();
        for (Object[] row : sqlRes) {
            AddressObject o = new AddressObject((String) row[1], (String)row[2], (Integer)row[0], (String) row[3]);
            result.add(o);
        }

        return result;
    }

    
    /**
     * 
     *      ADDRESS PART
     *
     */
    public static class AddressObject{

        private int aolevel = 0; 

        private String prefix = null;

        private String name = null;

        private String fiasCode = null;

        public AddressObject(String prefix, String name, int aolevel, String code) {
            this.aolevel = aolevel;
            this.prefix = prefix;
            this.name = name;
            this.fiasCode = code;
        }


        public String getPrefix() {
            return prefix;
        }

        public String getName() {
            return name;
        }

        public int getAolevel() {
            return aolevel;
        }


		public String getFiasCode() {
			return fiasCode;
		}
    }
}
