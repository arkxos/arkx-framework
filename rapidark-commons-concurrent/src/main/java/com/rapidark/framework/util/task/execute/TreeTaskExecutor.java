package com.rapidark.framework.util.task.execute;

import com.rapidark.framework.util.task.BaseTask;
import com.rapidark.framework.util.task.TaskContext;
import com.rapidark.framework.util.task.TaskStatus;
import com.rapidark.framework.util.task.TreeTask;

public class TreeTaskExecutor implements Runnable {

    protected final TreeTask task;

    public TreeTaskExecutor(TreeTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        if (task.setStatus(TaskStatus.QUEUED, TaskStatus.RUNNING)) {
            task.setStartTime(System.currentTimeMillis());
            TaskContext taskContext = createContext();
            try {
                task.getRunner().run(taskContext);
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
