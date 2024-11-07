package com.rapidark.framework.util.task;

@FunctionalInterface
public interface ResultTaskRunner<T> extends ITaskRunner {

    T run(TaskContext ctx) throws Exception;

}
