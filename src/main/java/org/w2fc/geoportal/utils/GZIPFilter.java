package org.w2fc.geoportal.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GZIPFilter implements Filter {

	private String excludePatterns = "";

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
		throws IOException, ServletException {
		if(req instanceof HttpServletRequest){
			String url = ((HttpServletRequest)req).getRequestURI().toString();
			if (matchExcludePatterns(url)) {
				chain.doFilter(req, res);
				return;
			}
		}
		if (req instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			String ae = request.getHeader("accept-encoding");
			if (ae != null && ae.indexOf("gzip") != -1) {
				GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response);
				chain.doFilter(req, wrappedResponse);
				wrappedResponse.finishResponse();
				return;
			}
			chain.doFilter(req, res);
		}
	}

	private boolean matchExcludePatterns(String url) {
		return url.matches(excludePatterns);
	}

	public void init(FilterConfig filterConfig) {
		this.excludePatterns = filterConfig.getInitParameter("excludePatterns").replaceAll("\\*", "(.*)");
	}

	public void destroy() {
		// noop
	}
}
