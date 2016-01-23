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

    @Bean
    public GeoUserDao geoUserDao(@Qualifier("sessionFactory") SessionFactory sessionFactory,
                                 @Qualifier("sessionFactoryCartography") SessionFactory sessionFactoryCartography) {

        return new TokenizedGeoUserDao(new GeoUserDaoImpl(sessionFactory, sessionFactoryCartography), sessionFactory);
    }
}


