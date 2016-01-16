package org.w2fc.geoportal.dao;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w2fc.geoportal.domain.GeoLayerType;


@Service
@Repository
public class GeoLayerTypeDaoImpl extends AbstractDaoDefaulImpl<GeoLayerType, Long> implements GeoLayerTypeDao {

    protected GeoLayerTypeDaoImpl() {
        super(GeoLayerType.class);
    }
}
