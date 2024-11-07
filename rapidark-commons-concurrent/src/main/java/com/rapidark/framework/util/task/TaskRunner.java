package com.rapidark.framework.util.task;

@FunctionalInterface
public interface TaskRunner extends ITaskRunner {

    void run(TaskContext ctx);

}
