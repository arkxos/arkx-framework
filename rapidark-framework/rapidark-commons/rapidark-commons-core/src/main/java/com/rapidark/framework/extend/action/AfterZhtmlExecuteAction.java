package com.rapidark.framework.extend.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rapidark.framework.extend.ExtendException;
import com.rapidark.framework.extend.IExtendAction;

/**
 * Zhtml页面执行后执行
 * 
 */
public abstract class AfterZhtmlExecuteAction implements IExtendAction {
	public static final String ExtendPointID = "com.rapidark.framework.AfterZhtmlExecuteAction";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		HttpServletRequest request = (HttpServletRequest) args[0];
		HttpServletResponse response = (HttpServletResponse) args[1];
		execute(request, response);
		return null;
	}

	public abstract void execute(HttpServletRequest request, HttpServletResponse response) throws ExtendException;
}
