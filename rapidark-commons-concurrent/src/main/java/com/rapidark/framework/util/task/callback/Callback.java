package com.rapidark.framework.util.task.callback;

import com.rapidark.framework.util.task.Context;

@FunctionalInterface
public interface Callback {

    void call(Context ctx, Exception error);

}
