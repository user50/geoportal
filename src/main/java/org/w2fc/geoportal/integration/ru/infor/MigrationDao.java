package org.w2fc.geoportal.integration.ru.infor;

import java.util.List;

import org.w2fc.geoportal.domain.GeoObject;

public interface MigrationDao {

    List<GeoObject> getObjectsByTags(List<String> tags);

    Boolean importByIds(Long layerId, List<String> objIds);

}
