package com.rapidark.framework.extend.action;

import javax.servlet.http.HttpSession;

import com.rapidark.framework.extend.ExtendException;
import com.rapidark.framework.extend.IExtendAction;

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
