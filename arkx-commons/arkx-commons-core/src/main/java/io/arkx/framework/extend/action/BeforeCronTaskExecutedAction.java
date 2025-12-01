package io.arkx.framework.extend.action;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

/**
 * 在定时任务执行前执行
 * 
 */
public abstract class BeforeCronTaskExecutedAction implements IExtendAction {
	public static final String ID = "io.arkx.framework.BeforeCronTaskExecutedAction";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		String taskManagerID = (String) args[0];
		String taskID = (String) args[1];
		execute(taskManagerID, taskID);
		return null;
	}

	public abstract void execute(String taskManagerID, String taskID);
}
