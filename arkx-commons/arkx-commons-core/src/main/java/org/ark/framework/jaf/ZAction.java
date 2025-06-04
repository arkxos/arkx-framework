package org.ark.framework.jaf;

import java.io.IOException;

import io.arkx.framework.cosyui.web.CookieData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @class org.ark.framework.jaf.ZAction
 * 
 * @author Darkness
 * @date 2013-1-31 上午11:29:48 
 * @version V1.0
 */
public class ZAction {
	
	private String forwardURL;
	private String redirectURL;
	private StringBuilder sb;
	private boolean BinaryMode;
	private CookieData cookies;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public CookieData getCookies() {
		return this.cookies;
	}

	public void setCookies(CookieData cookies) {
		this.cookies = cookies;
	}

	protected String getForwardURL() {
		return this.forwardURL;
	}

	public void forward(String forwardURL) {
		this.forwardURL = forwardURL;
		this.redirectURL = null;
	}

	protected String getRedirectURL() {
		return this.redirectURL;
	}

	public void redirect(String redirectURL) {
		this.redirectURL = redirectURL;
		this.forwardURL = null;
	}

	public boolean isBinaryMode() {
		return this.BinaryMode;
	}

	public void setBinaryMode(boolean flag) {
		this.BinaryMode = flag;
	}

	public void writeHTML(String html) {
		if (!this.BinaryMode) {
			if (this.sb == null) {
				this.sb = new StringBuilder();
			}
			this.sb.append(html);
		} else {
			throw new RuntimeException("Can't invoke writeHTML in binary mode!");
		}
	}

	public String getHTML() {
		return this.sb == null ? "" : this.sb.toString();
	}

	public void writeByte(byte[] arr) {
		if (this.BinaryMode)
			try {
				this.response.getOutputStream().write(arr);
				this.response.getOutputStream().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			throw new RuntimeException("Can't invoke writeByte in html mode!");
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public HttpServletRequest getRequest() {
		return this.request;
	}

	public HttpServletResponse getResponse() {
		return this.response;
	}

	public void setSkipPage(boolean skip) {
		this.request.setAttribute("ZACTION_SKIPPAGE", String.valueOf(skip));
	}
}