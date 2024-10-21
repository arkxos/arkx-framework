package org.ark.framework.jaf.zhtml;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlCommonServlet
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:54:07 
 * @version V1.0
 */
public class ZhtmlCommonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext context;
	private JspCommonServletConfig config;

	public ZhtmlCommonServlet(ServletContext context) {
		this.context = context;
	}

	public ServletConfig getServletConfig() {
		if (this.config == null) {
			this.config = new JspCommonServletConfig(this.context);
		}
		return this.config;
	}

	public static class JspCommonServletConfig implements ServletConfig {
		private ServletContext context;

		public JspCommonServletConfig(ServletContext context) {
			this.context = context;
		}

		public String getServletName() {
			return "JspCommon";
		}

		public ServletContext getServletContext() {
			return this.context;
		}

		public String getInitParameter(String name) {
			return null;
		}

		public Enumeration<String> getInitParameterNames() {
			return null;
		}
	}
}