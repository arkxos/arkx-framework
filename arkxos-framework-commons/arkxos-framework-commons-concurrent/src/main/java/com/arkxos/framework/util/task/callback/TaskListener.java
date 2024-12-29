package com.arkxos.framework.util.task.callback;

import com.arkxos.framework.util.task.TaskContext;

@FunctionalInterface
public interface TaskListener {

    void onExecuteFinish(TaskContext ctx, Exception error);

}
