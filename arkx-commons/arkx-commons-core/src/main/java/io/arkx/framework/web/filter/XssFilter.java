package io.arkx.framework.web.filter;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Loger
 */
public class XssFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		XssServletRequestWrapper xssRequestWrapper = new XssServletRequestWrapper(req);
		chain.doFilter(xssRequestWrapper, response);
	}

	@Override
	public void destroy() {

	}

}
