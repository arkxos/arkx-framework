package com.rapidark.framework.util.task;

@FunctionalInterface
public interface ResultExecutor<T> extends IExecutor {

    T execute(Context ctx) throws Exception;

}
