package org.ark.framework.extend.actions;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rapidark.framework.extend.ExtendException;
import com.rapidark.framework.extend.IExtendAction;


/**
 * @class org.ark.framework.extend.actions.AfterMainFilterAction
 * 框架主过滤器过滤后行为扩展
 * 
 * @author Darkness
 * @date 2012-8-6 下午10:30:17 
 * @version V1.0
 */
public abstract class AfterMainFilterAction implements IExtendAction {
	
	public static final String ExtendPointID = "org.ark.framework.AfterMainFilter";

	public Object execute(Object[] args) throws ExtendException {
		HttpServletRequest request = (HttpServletRequest) args[0];
		HttpServletResponse response = (HttpServletResponse) args[1];
		FilterChain chain = (FilterChain) args[2];
		return execute(request, response, chain);
	}

	public abstract boolean execute(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ExtendException;
}