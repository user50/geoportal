package org.w2fc.geoportal.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.w2fc.geoportal.dao.GeoObjectDao;
import org.w2fc.geoportal.dao.GeoUserDao;
import org.w2fc.geoportal.dao.OperationStatusRepository;
import org.w2fc.geoportal.ws.aspect.CreateObjectReportService;
import org.w2fc.geoportal.ws.aspect.CreateObjectsAspect;
import org.w2fc.geoportal.ws.aspect.ReportAspect;
import org.w2fc.geoportal.dao.OperationStatusRepositoryImpl;

/**
 * @author Yevhen
 */
@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    @Bean
    public ReportAspect loggingAspect(@Qualifier("sessionFactory") SessionFactory sessionFactoryy,
                                        @Qualifier("tokenizedGeoUserDao") GeoUserDao geoUserDao,
                                        GeoObjectDao geoObjectDao){
        OperationStatusRepository repository = new OperationStatusRepositoryImpl(sessionFactoryy);
        ReportAspect reportAspect = new ReportAspect(repository, geoUserDao,geoObjectDao);
        return reportAspect;
    }

    @Bean
    public CreateObjectsAspect createObjectsAspect(CreateObjectReportService reportService)
    {
        return new CreateObjectsAspect(reportService);
    }

    @Bean
    public CreateObjectReportService createObjectReportService(@Qualifier("sessionFactory") SessionFactory sessionFactoryy,
                                                               @Qualifier("tokenizedGeoUserDao") GeoUserDao geoUserDao)
    {
        OperationStatusRepository repository = new OperationStatusRepositoryImpl(sessionFactoryy);
        return new CreateObjectReportService(repository, geoUserDao);
    }


}
