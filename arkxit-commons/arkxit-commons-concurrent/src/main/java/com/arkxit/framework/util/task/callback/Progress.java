package com.arkxit.framework.util.task.callback;

import com.arkxit.framework.util.task.Task;

@FunctionalInterface
public interface Progress {

    void call(Task task, double progress);

}
