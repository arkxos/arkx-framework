package io.arkx.framework.util.task.callback;

import io.arkx.framework.util.task.TaskContext;

@FunctionalInterface
public interface TaskListener {

	void onExecuteFinish(TaskContext ctx, Exception error);

}
