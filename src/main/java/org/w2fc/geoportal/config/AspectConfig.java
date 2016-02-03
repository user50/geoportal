package org.w2fc.geoportal.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
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
    public OperationStatusRepository operationStatusRepository(@Qualifier("sessionFactory") SessionFactory sessionFactoryy){
        return new OperationStatusRepositoryImpl(sessionFactoryy);
    }

    @Bean
    public CreateObjectsAspect createObjectsAspect(CreateObjectReportService reportService)
    {
        return new CreateObjectsAspect(reportService);
    }

    @Bean
    public CreateObjectReportService createObjectReportService(OperationStatusRepository operationStatusRepository,
                                                               @Qualifier("tokenizedGeoUserDao") GeoUserDao geoUserDao)
    {
        return new CreateObjectReportService(operationStatusRepository, geoUserDao);
    }


}
