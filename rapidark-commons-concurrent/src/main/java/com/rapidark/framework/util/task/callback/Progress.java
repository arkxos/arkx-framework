package com.rapidark.framework.util.task.callback;

import com.rapidark.framework.util.task.Task;

@FunctionalInterface
public interface Progress {

    void call(Task task, double progress);

}
