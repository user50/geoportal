package org.w2fc.geoportal.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.dao.GeoUserDaoImpl;
import org.w2fc.geoportal.dao.TokenizedGeoUserDao;


@Configuration
public class GeoUserDaoConfig {

    @Bean(name = "tokenizedGeoUserDao")
    public GeoUserDao tokenizedGeoUserDao(@Qualifier("sessionFactory") SessionFactory sessionFactory,
                                 GeoUserDao geoUserDao) {

        return new TokenizedGeoUserDao(geoUserDao, sessionFactory);
    }
}


