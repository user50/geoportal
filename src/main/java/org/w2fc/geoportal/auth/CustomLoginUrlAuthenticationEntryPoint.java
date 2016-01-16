package org.w2fc.geoportal.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;


@Component
public class CustomLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    
    final Logger logger = LoggerFactory.getLogger(CustomLoginUrlAuthenticationEntryPoint.class);

    @SuppressWarnings("deprecation")
    public CustomLoginUrlAuthenticationEntryPoint() {
        super();
    }
    
    public CustomLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        
        String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
        if("XMLHttpRequest".equals(ajaxHeader)){
            ((HttpServletResponse)response).sendError(HttpStatus.FORBIDDEN.value(), "Access denied for this ajax request");
            logger.debug("Access denied for this ajax request. Ajax header : " + ajaxHeader);

        }else{
            super.commence(request, response, authException);
        }
        
    }
}
