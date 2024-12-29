package com.arkxos.framework.extend.action;

import com.arkxos.framework.extend.ExtendException;
import com.arkxos.framework.extend.IExtendAction;

/**
 * 在定时任务执行前执行
 * 
 */
public abstract class BeforeCronTaskExecutedAction implements IExtendAction {
	public static final String ID = "com.arkxos.framework.BeforeCronTaskExecutedAction";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		String taskManagerID = (String) args[0];
		String taskID = (String) args[1];
		execute(taskManagerID, taskID);
		return null;
	}

	public abstract void execute(String taskManagerID, String taskID);
}
