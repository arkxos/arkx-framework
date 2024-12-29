package com.arkxit.framework.util.task.callback;

import com.arkxit.framework.util.task.TaskContext;

@FunctionalInterface
public interface TaskListener {

    void onExecuteFinish(TaskContext ctx, Exception error);

}
