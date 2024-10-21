package org.ark.framework.jaf.zhtml;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspWriter;

/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlIncludeResponseWrapper
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:55:07 
 * @version V1.0
 */
public class ZhtmlIncludeResponseWrapper extends HttpServletResponseWrapper {
	private PrintWriter printWriter;
	private JspWriter jspWriter;
	private ServletResponse response;

	public ZhtmlIncludeResponseWrapper(ServletResponse response, JspWriter jspWriter) {
		super((HttpServletResponse) response);
		this.printWriter = new PrintWriter(jspWriter);
		this.response = response;
		this.jspWriter = jspWriter;
	}

	public PrintWriter getWriter() throws IOException {
		return this.printWriter;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return this.response.getOutputStream();
	}

	public void resetBuffer() {
		try {
			this.jspWriter.clearBuffer();
		} catch (IOException localIOException) {
		}
	}
}