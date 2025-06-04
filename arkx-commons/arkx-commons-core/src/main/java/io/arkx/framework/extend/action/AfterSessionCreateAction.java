package io.arkx.framework.extend.action;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

import jakarta.servlet.http.HttpSession;

/**
 * session创建行为扩展
 * 
 * @author Darkness
 * @date 2012-8-6 下午10:13:31 
 * @version V1.0
 */
public abstract class AfterSessionCreateAction implements IExtendAction {
	public static final String ExtendPointID = "com.arkxos.framework.AfterSessionCreate";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		HttpSession session = (HttpSession) args[0];
		execute(session);
		return null;
	}

	public abstract void execute(HttpSession session) throws ExtendException;
}
