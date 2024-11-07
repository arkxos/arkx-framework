package com.rapidark.framework.util.task;

@FunctionalInterface
public interface Executor extends IExecutor {

    void execute(Context ctx);

}
