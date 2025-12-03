package io.arkx.framework.util.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.arkx.framework.util.task.exception.TaskException;

public class ResultBaseTask<T> extends AbstractTask implements ResultTask<T> {

    private final ResultTaskRunner<T> executor;

    public ResultBaseTask(ResultTaskRunner<T> executor) {
        this(null, null, executor);
    }

    public ResultBaseTask(String type, String id, ResultTaskRunner<T> executor) {
        super(type, id);
        this.executor = executor;
    }

    public ResultTaskRunner<T> getExecutor() {
        return executor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        if (future != null) {
            try {
                return (T) future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new TaskException(e);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(long timeout, TimeUnit unit) throws TimeoutException {
        if (future != null) {
            try {
                return (T) future.get(timeout, unit);
            } catch (InterruptedException | ExecutionException e) {
                throw new TaskException(e);
            }
        }
        return null;
    }

}
