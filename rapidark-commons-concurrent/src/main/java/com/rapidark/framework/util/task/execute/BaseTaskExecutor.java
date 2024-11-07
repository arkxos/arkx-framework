package com.rapidark.framework.util.task.execute;

import com.rapidark.framework.util.task.BaseTask;
import com.rapidark.framework.util.task.TaskContext;
import com.rapidark.framework.util.task.TaskStatus;

public class BaseTaskExecutor implements Runnable {

    protected final BaseTask task;

    public BaseTaskExecutor(BaseTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        if (task.setStatus(TaskStatus.QUEUED, TaskStatus.RUNNING)) {
            task.setStartTime(System.currentTimeMillis());
            TaskContext taskContext = createContext();
            try {
                task.getExecutor().run(taskContext);
                taskContext.onSuccess();
            } catch (Exception e) {
                taskContext.onError(e);
                throw e;
            } finally {
                finallyExecute();
            }
        } else {
            finallyExecute();
        }
    }

    protected TaskContext createContext() {
        return new TaskContext(task);
    }

    protected void finallyExecute() {
        task.setEndTime(System.currentTimeMillis());
    }

}
