package com.arkxos.framework.extend.action;

import com.arkxos.framework.extend.ExtendException;
import com.arkxos.framework.extend.IExtendAction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
