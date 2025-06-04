package org.ark.framework.extend.actions;


import java.util.ArrayList;

import io.arkx.framework.cosyui.web.RequestData;

/**
 * @class org.ark.framework.extend.actions.JSPContext
 * jsp上下文
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:33:59
 * @version V1.0
 */
public class JSPContext {
	
	private StringBuilder sb = new StringBuilder();
	private ArrayList<String> includes = new ArrayList<String>();
	private RequestData request = null;

	public JSPContext(RequestData request) {
		this.request = request;
	}

	public RequestData getRequest() {
		return this.request;
	}

	protected String getOut() {
		return this.sb.toString();
	}

	protected ArrayList<String> getIncludes() {
		return this.includes;
	}

	public void write(Object obj) {
		this.sb.append(obj);
	}

	public void include(String file) {
		this.includes.add(file);
	}
}