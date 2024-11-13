package com.rapidark.framework.util.task.execute;

import com.rapidark.framework.util.task.BaseTask;
import com.rapidark.framework.util.task.TaskContext;
import com.rapidark.framework.util.task.TaskStatus;
import com.rapidark.framework.util.task.TreeTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TreeTaskExecutor implements Runnable {

    protected final TreeTask task;

    public TreeTaskExecutor(TreeTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        if (task.setStatus(TaskStatus.QUEUED, TaskStatus.RUNNING)) {
            task.setStartTime(System.nanoTime());
            TaskContext taskContext = createContext();
            try {
                log.debug("[" + Thread.currentThread().getName() + "]start task " + task.getClass().getSimpleName() + "-" + task.getId());
                task.getRunner().run(taskContext);
                taskContext.onSuccess();
                log.debug("[" + Thread.currentThread().getName() + "]end task " + task.getClass().getSimpleName() + "-" + task.getId());
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
        task.finish();
    }

}
