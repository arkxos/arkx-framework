package com.rapidark.framework.util.task.callback;

@FunctionalInterface
public interface Progress {

    void call(int progress);

}
