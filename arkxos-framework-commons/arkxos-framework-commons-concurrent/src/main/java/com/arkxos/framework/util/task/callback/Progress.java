package com.arkxos.framework.util.task.callback;

import com.arkxos.framework.util.task.Task;

@FunctionalInterface
public interface Progress {

    void call(Task task, double progress);

}
