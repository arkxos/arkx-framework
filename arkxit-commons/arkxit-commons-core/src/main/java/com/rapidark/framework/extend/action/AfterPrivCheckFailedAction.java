package com.rapidark.framework.extend.action;

import com.rapidark.framework.extend.ExtendException;
import com.rapidark.framework.extend.IExtendAction;

/**
 * 权限检查失败后行为
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:29:24 
 * @version V1.0
 */
public abstract class AfterPrivCheckFailedAction implements IExtendAction {
	public static final String ID = "com.rapidark.framework.AfterPrivCheckFailedAction";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		String message = (String) args[0];
		execute(message);
		return null;
	}

	public abstract void execute(String message);

}
