package org.ark.framework.extend.actions;


import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @class org.ark.framework.extend.actions.BeforeSSIFilterAction
 * ssi过滤之前行为
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:32:31
 * @version V1.0
 */
public abstract class BeforeSSIFilterAction implements IExtendAction {
	
	public static final String ExtendPointID = "org.ark.framework.BeforeSSIFilter";

	public Object execute(Object[] args) throws ExtendException {
		HttpServletRequest request = (HttpServletRequest) args[0];
		HttpServletResponse response = (HttpServletResponse) args[1];
		FilterChain chain = (FilterChain) args[2];
		execute(request, response, chain);
		return null;
	}

	public abstract void execute(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, FilterChain paramFilterChain) throws ExtendException;
}