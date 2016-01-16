package org.w2fc.geoportal.dao;

import java.util.List;

public interface UtilsDao {

    public static enum DataSources{
        PORTAL, CARTOGRAPHY
    }
    
    List<Object> exec(String query);
    List<Object> exec(String query, DataSources ds);
    List<?> execSql(String query, DataSources ds);

}
