package io.arkx.framework.util.task;

import io.arkx.framework.util.task.execute.BaseTaskExecutor;
import io.arkx.framework.util.task.execute.ResultTaskExecutor;
import io.arkx.framework.util.task.execute.TreeTaskExecutor;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class DefaultThreadPoolExecutor extends ThreadPoolExecutor {

    // 统计运行的任务总数量
    private final AtomicLong taskNumber = new AtomicLong(0);
    // 统计完成的任务总数量
    private final AtomicLong completedTaskNumber = new AtomicLong(0);
    private final Deque<Task> runningQueue = new ConcurrentLinkedDeque<>();
    private final Map<String, TaskGroup> runningTaskGrous = new ConcurrentHashMap<>();
//    private final LinkedBlockingDeque<Task> completedQueue = new LinkedBlockingDeque<>();

    private final CompletedTaskHandler completedTaskHandler;

    private final AtomicInteger globalExecuteOrder = new AtomicInteger();
    private List<Task> waitingTasks = new CopyOnWriteArrayList<>();

    public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                     BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                     RejectedExecutionHandler handler, CompletedTaskHandler completedTaskHandler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        if (completedTaskHandler == null) {
            this.completedTaskHandler = new DefaultCompletedTaskHandler();
        } else {
            this.completedTaskHandler = completedTaskHandler;
        }
//        startHandleCompletedTask();
    }

    public void submit(Task task) {
        this.waitingTasks.add(task);
//        this.executeTask(task);
        this.loopForExecute();
    }

    private void loopForExecute() {
        while(true) {
            long lastThreadCheckTime = System.currentTimeMillis();

            int poolSize = this.getCorePoolSize();
            int activeCount = this.getRunningNumberofTask();
            int availableCount = poolSize - activeCount;

            if (availableCount <= 0 || waitingTasks.isEmpty()) {
                return;
            }
            Task task = waitingTasks.get(0);

            AbstractTask needExecute;
            if (task instanceof TreeTask treeTask) {
                if (System.currentTimeMillis() - lastThreadCheckTime > 60_000) {// 3秒查一次
                    treeTask.print();

                    lastThreadCheckTime = System.currentTimeMillis();
                }

                boolean taskCompleted = treeTask.isFinished();
                needExecute = treeTask.findNeedExecuteTask();
                if(needExecute == null) {
                    if(taskCompleted) {
                        waitingTasks.remove(0);
                    }
                    return;
                } else {
                    needExecute.setGlobalExecuteOrder(globalExecuteOrder.incrementAndGet());
                    executeTask(needExecute);
                }

                // 此线程任务很轻，在此计算任务进度并触发进度监控
//                        caculateTaskPercentAndNotifice(treeTask);
            } else {
                waitingTasks.remove(0);
//                        needExecute = task;
                executeTask(task);
            }
        }
    }

    public void executeTask(Task task) {
        if (task instanceof BaseTask bTask) {
            BaseTaskExecutor baseTaskExecutor = new BaseTaskExecutor(bTask);
            RunnableFuture<Object> future = newTaskFor(bTask, baseTaskExecutor);
            bTask.setFuture(future);
            bTask.setStatus(TaskStatus.INIT, TaskStatus.QUEUED);
            execute(future);
        } else if (task instanceof TreeTask treeTask) {
            TreeTaskExecutor baseTaskExecutor = new TreeTaskExecutor(treeTask);
            RunnableFuture<Object> future = newTaskFor(treeTask, baseTaskExecutor);
            treeTask.setFuture(future);
            treeTask.setStatus(TaskStatus.INIT, TaskStatus.QUEUED);
            execute(future);
        } else if (task instanceof ResultBaseTask<?> bTask) {
            ResultTaskExecutor<?> executor = new ResultTaskExecutor<>(bTask);
            RunnableFuture<?> future = newTaskFor(bTask, executor);
            bTask.setFuture(future);
            bTask.setStatus(TaskStatus.INIT, TaskStatus.QUEUED);
            execute(future);
        }

        runningQueue.offer(task);
    }

    public void submit(TaskGroup.Item item, TaskGroup group) {
        TaskGroup.GroupItemExecutorBase executor = new TaskGroup.GroupItemExecutorBase(item, group);
        RunnableFuture<Object> future = newTaskFor(item, executor);
        item.setFuture(future);
        item.setStatus(TaskStatus.INIT, TaskStatus.QUEUED);
        execute(future);
        runningQueue.offer(item);
    }

    private <T> RunnableFuture<T> newTaskFor(Task task, Callable<T> callable) {
        return new CustomFutureTask<>(task, callable);
    }

    private <T> RunnableFuture<T> newTaskFor(Task task, Runnable runnable) {
        return new CustomFutureTask<>(task, runnable, null);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        taskNumber.incrementAndGet();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (r instanceof CustomFutureTask<?> futureTask) {
            Task task = futureTask.getTask();
            runningQueue.remove(task);
//            completedQueue.offer(task);
            completedTaskNumber.incrementAndGet();

            this.loopForExecute();
        }
    }

//    private void startHandleCompletedTask() {
//        new Thread(() -> {
//            while (true) {
//                try {
//                    Task take = completedQueue.take();
//                    DefaultThreadPoolExecutor.this.completedTaskHandler.handle(take);
//                } catch (Throwable ignore) {
//                }
//            }
//        }).start();
//    }

    // 获取正在运行的任务，包含任务组中的任务
    public final List<Task> getRunningTasks() {
        return new LinkedList<>(runningQueue);
    }

    // 获取正在运行的任务总量，包含任务组中的任务
    public int getRunningNumberofTask() {
        return runningQueue.size();
    }

    // 获取已经完成的任务总量，包含任务组中的任务
    public long getCompletedNumberOfTask() {
        return completedTaskNumber.get();
    }

    // 获取执行的任务总量
    public long getTotalNumberOfTask() {
        return taskNumber.get();
    }

    public void addTaskGroup(TaskGroup taskGroup) {
        this.runningTaskGrous.put(taskGroup.getId(), taskGroup);
    }

    protected void removeTaskGroup(TaskGroup taskGroup) {
        this.runningTaskGrous.remove(taskGroup.getId());
    }

    // 获取正在运行的任务组
    public Collection<TaskGroup> getRunningTaskGroups() {
        return new LinkedList<>(this.runningTaskGrous.values());
    }

    private static class CustomFutureTask<V> extends FutureTask<V> {

        private final Task task;

        public CustomFutureTask(Task task, Callable<V> callable) {
            super(callable);
            this.task = task;
        }

        public CustomFutureTask(Task task, Runnable runnable, V result) {
            super(runnable, result);
            this.task = task;
        }

        public final Task getTask() {
            return task;
        }
    }

}
