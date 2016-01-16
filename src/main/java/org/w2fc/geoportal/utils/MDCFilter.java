package org.w2fc.geoportal.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
 
public class MDCFilter implements Filter {
 
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }
 
  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
          throws IOException, ServletException {
    Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      MDC.put("userName", authentication.getName());
      Object details = authentication.getDetails();
      String realIP = null;
      try{
    	  realIP = ((HttpServletRequest)req).getHeader("X-Real-IP");
      }catch(Exception e){
    	  //TODO: logging
      }
	  if(details instanceof WebAuthenticationDetails){
    	  MDC.put("ip", (realIP != null)?realIP:((WebAuthenticationDetails)details).getRemoteAddress());
      }
    }
    try {
      chain.doFilter(req, resp);
    } finally {
      if (authentication != null) {
        MDC.remove("userName");
      }
    }
  }
 
  @Override
  public void destroy() {
  }
 
}
