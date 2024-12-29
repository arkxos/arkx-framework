package com.rapidark.framework.extend.action;

import com.rapidark.framework.extend.ExtendException;
import com.rapidark.framework.extend.IExtendAction;

/**
 * UI方法调用后执行
 * @author Darkness
 * @date 2012-8-7 下午9:30:31 
 * @version V1.0
 */
public abstract class AfterUIMethodInvokeAction implements IExtendAction {
	public static final String ExtendPointID = "com.rapidark.framework.AfterUIMethodInvoke";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		execute((String) args[0]);
		return null;
	}

	public abstract void execute(String method) throws ExtendException;
}
