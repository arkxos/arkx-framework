package com.arkxos.framework.cosyui.web;

import java.util.Enumeration;

import com.arkxos.framework.commons.collection.CaseIgnoreMapx;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.data.db.DataCollection;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 封装一次http请求中的数据，可以在JAVA代码中直接创建实例。
 * @author Darkness
 * @date 2012-8-5 下午11:06:09 
 * @version V1.0
 */
public class RequestData extends DataCollection {
	
	private static final long serialVersionUID = 1L;

	private Mapx<String, Object> headers;

	protected HttpServletRequest servletRequest;// 非servlet环境此值为null

	private CookieData cookies = null;

	private String URL;
	private String realPath;
	private String queryString;

	private String clientIP;

	private String className;

	private String serverName;

	private String httpMethod;

	private int port;

	private String scheme;

	private String sessionID;
	
	/**
	 * 返回当前请求的域名
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * 设置当前请求的域名，仅框架和测试代码需要调用本方法。
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * 返回当前请求URL中的端口
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 设置当前请求的端口，仅框架和测试代码需要调用本方法。
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 返回当前请求中的协议，如http/https
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * 设置当前请求的协议，仅框架和测试代码需要调用本方法。
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	/**
	 * 返回当前UIFacade类的类名
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * 设置当前请求的类名，仅框架和测试代码需要调用本方法。
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * 返回客户端IP地址
	 */
	public String getClientIP() {
		if ("0:0:0:0:0:0:0:1".equals(this.clientIP)) {
			this.clientIP = "127.0.0.1";
		}
		return this.clientIP;
	}

	/**
	 * 设置当前请求的客户端IP地址，仅框架和测试代码需要调用本方法。
	 */
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	/**
	 * 返回当前请求的URL
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * 设置当前请求的URL，仅框架和测试代码需要调用本方法。
	 */
	public void setURL(String url) {
		URL = url;
	}

	/**
	 * 获取http头
	 */
	public Mapx<String, Object> getHeaders() {
		if (headers == null) {
			headers = new CaseIgnoreMapx<>();
			if (servletRequest != null) {
				Enumeration<?> e = servletRequest.getHeaderNames();
				while (e.hasMoreElements()) {
					String k = e.nextElement().toString();
					headers.put(k, servletRequest.getHeader(k));
				}
			}
		}
		return headers;
	}

	/**
	 * 设置http头，仅框架和测试代码需要调用本方法。
	 */
	public void setHeaders(Mapx<String, Object> headers) {
		this.headers = headers;
	}

	/**
	 * 获取当前请求的QueryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * 设置当前请求的QueryString，仅框架和测试代码需要调用本方法。
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * 获取Cookie数据对象
	 */
	public CookieData getCookies() {
		if (cookies == null) {
			if (servletRequest == null) {
				cookies = new CookieData();
			} else {
				cookies = new CookieData(servletRequest);
			}
		}
		return cookies;
	}

	/**
	 * 设置Cookie数据对象，仅框架和测试代码需要调用本方法。
	 */
	public void setCookies(CookieData cookies) {
		this.cookies = cookies;
	}

	/**
	 * 返回当前HttpSession对应的ID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * 将当前HttpSession对应的ID赋值给RequestData，仅框架和测试代码需要调用本方法。
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * 返回当前的http方法
	 */
	public String getHttpMethod() {
		return httpMethod;
	}

	/**
	 * 设置当前的http方法，仅框架和测试代码需要调用本方法。
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.servletRequest = request;
	}
	
	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	/**
	 * 清除数据以便复用
	 */
	@Override
	public void clear() {
		this.headers = null;
		this.cookies = null;
		this.servletRequest = null;
		this.sessionID = null;
		this.URL = null;
		this.queryString = null;
		super.clear();
	}

	public String getRealPath() {
		return realPath;
	}

	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}
	
	/**
	 * 通过覆盖此方法确保大小写字符串取到一样的hash值
	 */
	
	@Override
	protected int hash(Object key) {
		if (key instanceof String) {
			return CaseIgnoreMapx.caseIgnoreHash((String) key);
		} else {
			return hash(key.hashCode());
		}
	}

	/**
	 * 覆盖此方法以实现忽略大小写的字符串比较
	 */
	@Override
	protected boolean eq(Object x, int hash, Entry<String, Object> e) {
		if (x == e.getKey()) {
			return true;
		}
		if (x instanceof String) {
			return hash == e.getHash();
		}
		return x.equals(e.getKey());
	}
}
