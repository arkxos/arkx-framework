package com.rapidark.framework.util.task.execute;

import com.rapidark.framework.util.task.ResultBaseTask;
import com.rapidark.framework.util.task.TaskContext;
import com.rapidark.framework.util.task.TaskStatus;

import java.util.concurrent.Callable;

public class ResultTaskExecutor<T> implements Callable<T> {

    private final ResultBaseTask<T> task;

    public ResultTaskExecutor(ResultBaseTask<T> task) {
        this.task = task;
    }

    @Override
    public T call() throws Exception {
        task.setStartTime(System.nanoTime());
        if (task.setStatus(TaskStatus.QUEUED, TaskStatus.RUNNING)) {
            TaskContext taskContext = createContext();
            try {
                T result = task.getExecutor().run(taskContext);
                taskContext.onSuccess();
                return result;
            } finally {
                finallyExecute();
            }
        } else {
            finallyExecute();
            return null;
        }
    }

    protected TaskContext createContext() {
        return new TaskContext(task);
    }

    protected void finallyExecute() {
        task.setEndTime(System.nanoTime());
    }

}
