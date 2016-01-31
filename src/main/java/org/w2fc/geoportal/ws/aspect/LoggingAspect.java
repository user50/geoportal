package org.w2fc.geoportal.ws.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author Yevhen
 */
@Component
@Aspect
public class LoggingAspect {
    @Before("execution(public * org.w2fc.geoportal.ws.GeoObjectsService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("logBefore() is running!");
        System.out.println("method : " + joinPoint.getSignature().getName());
        System.out.println("******");
    }

    @After("execution(public * org.w2fc.geoportal.ws.GeoObjectsService.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("logAfter() is running!");
        System.out.println("method : " + joinPoint.getSignature().getName());
        System.out.println("******");
    }
}
