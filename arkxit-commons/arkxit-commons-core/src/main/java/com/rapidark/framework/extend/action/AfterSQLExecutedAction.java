package com.rapidark.framework.extend.action;

import com.rapidark.framework.extend.ExtendException;
import com.rapidark.framework.extend.IExtendAction;

/**
 * SQL执行之后行为
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:30:00 
 * @version V1.0
 */
public abstract class AfterSQLExecutedAction implements IExtendAction {
	public static final String ID = "com.rapidark.framework.AfterSQLExecutedAction";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		long costTime = (Long) args[0];
		String message = (String) args[1];
		execute(costTime, message);
		return null;
	}

	public abstract void execute(long costTime, String message);
}
