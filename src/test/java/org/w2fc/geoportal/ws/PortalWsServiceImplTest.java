package org.w2fc.geoportal.ws;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.w2fc.geoportal.utils.ServiceRegistry;

import static org.junit.Assert.*;

public class PortalWsServiceImplTest {

    @org.junit.Test
    public void testCreatePoint() throws Exception {
        System.out.println(new BCryptPasswordEncoder().encode("neuser50"));

    }
}