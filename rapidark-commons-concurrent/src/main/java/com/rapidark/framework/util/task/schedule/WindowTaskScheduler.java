package com.rapidark.framework.util.task.schedule;

import com.rapidark.framework.util.task.DefaultThreadPoolExecutor;
import com.rapidark.framework.util.task.Task;
import com.rapidark.framework.util.task.TaskGroup;
import com.rapidark.framework.util.task.TaskScheduler;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WindowTaskScheduler implements TaskScheduler {

    @Getter
    private final DefaultThreadPoolExecutor executor;

    public WindowTaskScheduler(DefaultThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void submit(Task task) {
        this.executor.submit(task);
    }

    @Override
    public void submit(TaskGroup.Item item, TaskGroup group) {
        this.executor.submit(item, group);
    }

    @Override
    public void addTaskGroup(TaskGroup taskGroup) {
        this.executor.addTaskGroup(taskGroup);
    }

    @Override
    public boolean isShutdown() {
        return this.executor.isShutdown();
    }

    @Override
    public void shutdown() {
        this.executor.shutdown();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.executor.awaitTermination(timeout, unit);
    }

    @Override
    public void shutdownNow() {
        this.executor.shutdownNow();
    }

    @Override
    public List<Task> getRunningTasks() {
        return this.executor.getRunningTasks();
    }

    @Override
    public int getRunningNumberofTask() {
        return this.executor.getRunningNumberofTask();
    }

    @Override
    public long getCompletedNumberOfTask() {
        return this.executor.getCompletedNumberOfTask();
    }

    @Override
    public long getTotalNumberOfTask() {
        return this.executor.getTotalNumberOfTask();
    }

    @Override
    public Collection<TaskGroup> getRunningTaskGroups() {
        return this.executor.getRunningTaskGroups();
    }

}
