package com.rapidark.framework.util.task;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface TaskScheduler {
    void submit(Task task);

    void submit(TaskGroup.Item item, TaskGroup group);

    void addTaskGroup(TaskGroup taskGroup);

    boolean isShutdown();

    void shutdown();

    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    void shutdownNow();

    List<Task> getRunningTasks();

    int getRunningNumberofTask();

    long getCompletedNumberOfTask();

    long getTotalNumberOfTask();

    Collection<TaskGroup> getRunningTaskGroups();
}
