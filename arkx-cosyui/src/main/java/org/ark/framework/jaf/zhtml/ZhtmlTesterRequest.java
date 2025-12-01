package org.ark.framework.jaf.zhtml;

import io.arkx.framework.Account;
import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.Enumerator;
import io.arkx.framework.commons.util.ObjectUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;


/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlTesterRequest
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:56:28 
 * @version V1.0
 */
public class ZhtmlTesterRequest implements HttpServletRequest {
	private HashMap<String, Object> attributes = new HashMap<String, Object>();
	private HashMap<String, String> headers = new HashMap<String, String>();
	private HashMap<String, String> parameters = new HashMap<String, String>();
	private String charset;
	private int port;
	private String remoteAddr;
	private String remoteHost;
	private String contextPath;
	private String queryString;
	private String servletPath;
	private String protocol;
	private String method;
	private ZhtmlPage page;

	public ZhtmlTesterRequest(ZhtmlPage page) {
		this.page = page;
	}

	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}

	public Enumeration<String> getAttributeNames() {
		return new Enumerator(this.attributes.keySet(), true);
	}

	public String getCharacterEncoding() {
		if (this.charset == null) {
			this.charset = Config.getGlobalCharset();
		}
		return this.charset;
	}

	public int getContentLength() {
		return 0;
	}

	public String getContentType() {
		return "text/html";
	}

	public ServletInputStream getInputStream() throws IOException {
		return null;
	}

	public String getLocalAddr() {
		return "127.0.0.1";
	}

	public String getLocalName() {
		return "localhost";
	}

	public int getLocalPort() {
		return this.port;
	}

	public Locale getLocale() {
		return Locale.CHINA;
	}


	public String getParameter(String key) {
		return (String) this.parameters.get(key);
	}

	public Enumeration<String> getParameterNames() {
		return new Enumerator(this.parameters.keySet(), true);
	}

	public String[] getParameterValues(String arg0) {
		String[] arr = new String[this.parameters.size()];
		this.parameters.values().toArray(arr);
		return arr;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public BufferedReader getReader() throws IOException {
		return null;
	}

	public String getRealPath(String path) {
		return this.page.getPageContext().getServletContext().getRealPath(path);
	}

	public String getRemoteAddr() {
		return this.remoteAddr;
	}

	public String getRemoteHost() {
		return this.remoteHost;
	}

	public int getRemotePort() {
		return this.port;
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		return null;
	}

	public String getScheme() {
		return this.protocol;
	}

	public String getServerName() {
		return "localhost";
	}

	public int getServerPort() {
		return this.port;
	}

	public boolean isSecure() {
		return this.protocol.equals("https");
	}

	public void removeAttribute(String key) {
		this.attributes.remove(key);
	}

	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}

	public void setCharacterEncoding(String charset) throws UnsupportedEncodingException {
		this.charset = charset;
	}

	public String getAuthType() {
		return null;
	}

	public String getContextPath() {
		return this.contextPath;
	}

	public Cookie[] getCookies() {
		return null;
	}

	public long getDateHeader(String key) {
		return Long.parseLong(getHeader(key));
	}

	public String getHeader(String key) {
		return (String) this.headers.get(key);
	}

	public Enumeration<String> getHeaderNames() {
		return new Enumerator(this.headers.keySet(), true);
	}

	public Enumeration<String> getHeaders(String key) {
		return new Enumerator(this.headers.keySet(), true);
	}

	public int getIntHeader(String arg0) {
		return 0;
	}

	public String getMethod() {
		return this.method;
	}

	public String getPathInfo() {
		return this.servletPath;
	}

	public String getPathTranslated() {
		return this.servletPath;
	}

	public String getQueryString() {
		return this.queryString;
	}

	public String getRemoteUser() {
		return null;
	}

	public String getRequestURI() {
		String url = this.protocol + "://" + this.remoteHost + ":" + this.port + "/" + this.contextPath + "/" + this.servletPath;
		if (ObjectUtil.notEmpty(this.queryString)) {
			url = url + "?" + this.queryString;
		}
		return url;
	}

	public StringBuffer getRequestURL() {
		return new StringBuffer(getRequestURI());
	}

	public String getRequestedSessionId() {
		return Account.getSessionID();
	}

	public String getServletPath() {
		return this.servletPath;
	}

	public HttpSession getSession() {
		return null;
	}

	public HttpSession getSession(boolean arg0) {
		return getSession();
	}

	public Principal getUserPrincipal() {
		return null;
	}

	public boolean isRequestedSessionIdFromCookie() {
		return true;
	}

	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	public boolean isRequestedSessionIdValid() {
		return true;
	}

	public boolean isUserInRole(String user) {
		return false;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public long getContentLengthLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestId() {
		return "";
	}

	@Override
	public String getProtocolRequestId() {
		return "";
	}

	@Override
	public ServletConnection getServletConnection() {
		return null;
	}

	@Override
	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(String username, String password) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		// TODO Auto-generated method stub
		return null;
	}
}