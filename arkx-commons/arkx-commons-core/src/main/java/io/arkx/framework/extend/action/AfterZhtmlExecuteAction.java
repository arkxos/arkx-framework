package io.arkx.framework.extend.action;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Zhtml页面执行后执行
 *
 */
public abstract class AfterZhtmlExecuteAction implements IExtendAction {

	public static final String ExtendPointID = "io.arkx.framework.AfterZhtmlExecuteAction";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		HttpServletRequest request = (HttpServletRequest) args[0];
		HttpServletResponse response = (HttpServletResponse) args[1];
		execute(request, response);
		return null;
	}

	public abstract void execute(HttpServletRequest request, HttpServletResponse response) throws ExtendException;

}
