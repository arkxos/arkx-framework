package org.ark.framework.jaf.zhtml;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlTesterPageContext
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:56:18 
 * @version V1.0
 */
public class ZhtmlTesterPageContext extends PageContext {
	private ZhtmlPage page;
	private ServletContext servletContext = new ZhtmlTesterServletContext(this.page);

	public ZhtmlTesterPageContext(ZhtmlPage page) {
		this.page = page;
	}

	public void initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) throws IOException,
			IllegalStateException, IllegalArgumentException {
	}

	public void release() {
	}

	public HttpSession getSession() {
		return null;
	}

	public Object getPage() {
		return null;
	}

	public ServletRequest getRequest() {
		return this.page.getRequest();
	}

	public ServletResponse getResponse() {
		return this.page.getResponse();
	}

	public Exception getException() {
		return null;
	}

	public ServletConfig getServletConfig() {
		return new ServletConfig() {
			public String getInitParameter(String arg0) {
				return null;
			}

			public Enumeration<String> getInitParameterNames() {
				return null;
			}

			public ServletContext getServletContext() {
				return getServletContext();
			}

			public String getServletName() {
				return ZhtmlTesterPageContext.this.page.getRequest().getServletPath();
			}
		};
	}

	public ServletContext getServletContext() {
		return this.servletContext;
	}

	public void forward(String relativeUrlPath) throws ServletException, IOException {
	}

	public void include(String relativeUrlPath) throws ServletException, IOException {
	}

	public void include(String relativeUrlPath, boolean flush) throws ServletException, IOException {
	}

	public void handlePageException(Exception e) throws ServletException, IOException {
	}

	public void handlePageException(Throwable t) throws ServletException, IOException {
	}

	public void setAttribute(String name, Object value) {
	}

	public void setAttribute(String name, Object value, int scope) {
	}

	public Object getAttribute(String name) {
		return null;
	}

	public Object getAttribute(String name, int scope) {
		return null;
	}

	public Object findAttribute(String name) {
		return null;
	}

	public void removeAttribute(String name) {
	}

	public void removeAttribute(String name, int scope) {
	}

	public int getAttributesScope(String name) {
		return 0;
	}

	public Enumeration<String> getAttributeNamesInScope(int scope) {
		return null;
	}

	public JspWriter getOut() {
		return null;
	}

	public ExpressionEvaluator getExpressionEvaluator() {
		return null;
	}

	public VariableResolver getVariableResolver() {
		return null;
	}
}