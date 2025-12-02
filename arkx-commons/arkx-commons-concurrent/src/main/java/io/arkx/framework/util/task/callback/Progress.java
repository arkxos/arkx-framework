package io.arkx.framework.util.task.callback;

import io.arkx.framework.util.task.Task;

@FunctionalInterface
public interface Progress {

	void call(Task task, double progress);

}
