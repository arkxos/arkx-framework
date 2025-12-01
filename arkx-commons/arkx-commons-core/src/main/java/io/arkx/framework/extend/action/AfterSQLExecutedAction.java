package io.arkx.framework.extend.action;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

/**
 * SQL执行之后行为
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:30:00 
 * @version V1.0
 */
public abstract class AfterSQLExecutedAction implements IExtendAction {
	public static final String ID = "io.arkx.framework.AfterSQLExecutedAction";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		long costTime = (Long) args[0];
		String message = (String) args[1];
		execute(costTime, message);
		return null;
	}

	public abstract void execute(long costTime, String message);
}
