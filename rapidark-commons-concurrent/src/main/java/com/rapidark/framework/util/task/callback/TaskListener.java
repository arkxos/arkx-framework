package com.rapidark.framework.util.task.callback;

import com.rapidark.framework.util.task.TaskContext;

@FunctionalInterface
public interface TaskListener {

    void onExecuteFinish(TaskContext ctx, Exception error);

}
