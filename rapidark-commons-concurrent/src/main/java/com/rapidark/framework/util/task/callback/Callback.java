package com.rapidark.framework.util.task.callback;

import com.rapidark.framework.util.task.TaskContext;

@FunctionalInterface
public interface Callback {

    void call(TaskContext ctx, Exception error);

}
