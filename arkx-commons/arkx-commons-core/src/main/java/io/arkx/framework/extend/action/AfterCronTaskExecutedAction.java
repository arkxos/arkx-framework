package io.arkx.framework.extend.action;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

/**
 * 定时任务执行之后行为
 *
 * @author Darkness
 * @date 2012-8-7 下午9:28:02
 * @version V1.0
 */
public abstract class AfterCronTaskExecutedAction implements IExtendAction {

    public static final String ID = "io.arkx.framework.AfterCronTaskExecutedAction";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        String taskManagerID = (String) args[0];
        String taskID = (String) args[1];
        execute(taskManagerID, taskID);
        return null;
    }

    public abstract void execute(String taskManagerID, String taskID);

}
