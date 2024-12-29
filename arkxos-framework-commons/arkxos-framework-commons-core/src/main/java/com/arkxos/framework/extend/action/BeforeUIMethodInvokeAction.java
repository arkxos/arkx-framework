package com.arkxos.framework.extend.action;

import com.arkxos.framework.extend.ExtendException;
import com.arkxos.framework.extend.IExtendAction;

/**
 * UI方法调用之前执行
 * @author Darkness
 * @date 2012-8-7 下午9:32:54 
 * @version V1.0
 */
public abstract class BeforeUIMethodInvokeAction implements IExtendAction {
	public static final String ExtendPointID = "com.arkxos.framework.BeforeUIMethodInvoke";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		execute((String) args[0]);
		return null;
	}

	public abstract void execute(String method) throws ExtendException;
}
