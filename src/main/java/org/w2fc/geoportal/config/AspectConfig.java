package org.w2fc.geoportal.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.w2fc.geoportal.dao.OperationStatusRepository;
import org.w2fc.geoportal.ws.aspect.ReportAspect;
import org.w2fc.geoportal.dao.OperationStatusRepositoryImpl;

/**
 * @author Yevhen
 */
@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    @Bean
    public ReportAspect loggingAspect(@Qualifier("sessionFactory") SessionFactory sessionFactoryy){
        OperationStatusRepository repository = new OperationStatusRepositoryImpl(sessionFactoryy);
        ReportAspect reportAspect = new ReportAspect(repository);
        return reportAspect;
    }
}
