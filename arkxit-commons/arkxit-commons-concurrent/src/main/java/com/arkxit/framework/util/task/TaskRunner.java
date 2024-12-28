package com.arkxit.framework.util.task;

@FunctionalInterface
public interface TaskRunner extends ITaskRunner {

    void run(TaskContext ctx);

}
