package com.rapidark.framework.util.task.schedule;

import com.rapidark.framework.util.task.*;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class WindowTaskScheduler implements TaskScheduler {

    @Getter
    private final DefaultThreadPoolExecutor executor;
    private List<Task> waitingTasks = new CopyOnWriteArrayList<>();
    private Thread loopThread;
    private boolean isNeedRun = true;

    public WindowTaskScheduler(DefaultThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void start() {
        int poolSize = this.executor.getCorePoolSize();
        int activeCount = this.executor.getActiveCount();
        int availableCount = poolSize - activeCount;

        System.currentTimeMillis();

        loopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long lastThreadCheckTime = System.currentTimeMillis();
                while (isNeedRun) {
                    if(availableCount <= 0 || waitingTasks.isEmpty()) {
                        continue;
                    }
                    Task task = waitingTasks.get(0);

                    Task needExecute;
                    if(task instanceof TreeTask treeTask) {
                        if (System.currentTimeMillis() - lastThreadCheckTime > 60_000) {// 3秒查一次
                            treeTask.print();
                        }

                        needExecute = treeTask.findNeedExecuteTask();

                        boolean taskCompleted = treeTask.isFinished();

                        if(needExecute == null && taskCompleted) {
                            waitingTasks.remove(0);
                        } else if(needExecute != null) {
                            executor.submit(needExecute);
                        }


                        task.triggerCompleted();

                        // 此线程任务很轻，在此计算任务进度并触发进度监控
                        caculateTaskPercentAndNotifice(treeTask);
                    } else {
                        waitingTasks.remove(0);
                        needExecute = task;
                        executor.submit(needExecute);
                    }
                }
            }
        });
        loopThread.start();
    }

    private Map<String, Double> taskProgressPercentMap = new ConcurrentHashMap<>();
    private void caculateTaskPercentAndNotifice(TreeTask treeTask) {
        treeTask.caculateProgressPercent();
        comparePercentListener(treeTask);
    }

    private void comparePercentListener(TreeTask treeTask) {
        String taskId = treeTask.getId();
        if(!taskProgressPercentMap.containsKey(taskId)) {
            taskProgressPercentMap.put(taskId, treeTask.getProgressPercent());
            triggerPercentListener(treeTask);
        } else {
            boolean isSame = taskProgressPercentMap.get(taskId) == treeTask.getProgressPercent();
            if(!isSame) {
                taskProgressPercentMap.put(taskId, treeTask.getProgressPercent());
                triggerPercentListener(treeTask);
            }
        }
        for (TreeTask child : treeTask.getChildren()) {
            comparePercentListener(child);
        }
    }

    private void triggerPercentListener(TreeTask treeTask) {
        if(treeTask.getProgress() != null) {
            treeTask.getProgress().call(treeTask, treeTask.getProgressPercent());
        }
    }

    @Override
    public void submit(Task task) {
        this.waitingTasks.add(task);
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
        this.isNeedRun = false;
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
