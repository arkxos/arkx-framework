package com.arkxit.framework.util.task.execute;

import com.arkxit.framework.util.task.BaseTask;
import com.arkxit.framework.util.task.TaskContext;
import com.arkxit.framework.util.task.TaskStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseTaskExecutor implements Runnable {

    protected final BaseTask task;

    public BaseTaskExecutor(BaseTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        if (task.setStatus(TaskStatus.QUEUED, TaskStatus.RUNNING)) {
            task.setStartTime(System.nanoTime());
            TaskContext taskContext = createContext();
            try {
                log.debug("start task " + task.getClass().getSimpleName());
                task.getRunner().run(taskContext);
                taskContext.onSuccess();
                log.debug("end task " + task.getClass().getSimpleName());
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
        task.setEndTime(System.nanoTime());
    }

}
