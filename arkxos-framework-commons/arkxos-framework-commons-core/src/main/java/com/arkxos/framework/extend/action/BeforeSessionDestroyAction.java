package com.arkxos.framework.extend.action;

import com.arkxos.framework.extend.ExtendException;
import com.arkxos.framework.extend.IExtendAction;

import jakarta.servlet.http.HttpSession;

/**
 * Session销毁之前执行
 * @author Darkness
 * @date 2012-8-6 下午10:21:21 
 * @version V1.0
 */
public abstract class BeforeSessionDestroyAction implements IExtendAction {
	public static final String ExtendPointID = "com.rapidark.framework.BeforeSessionDestory";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		HttpSession session = (HttpSession) args[0];
		execute(session);
		return null;
	}

	public abstract void execute(HttpSession session) throws ExtendException;
}
