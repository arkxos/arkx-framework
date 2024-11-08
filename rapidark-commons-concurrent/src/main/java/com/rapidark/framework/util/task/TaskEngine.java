package com.rapidark.framework.util.task;

import com.rapidark.framework.util.task.schedule.DefaultTaskScheduler;
import com.rapidark.framework.util.task.schedule.WindowTaskScheduler;
import com.rapidark.framework.util.task.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class TaskEngine {

    /**
     * 默认为 CPU 核数
     */
    private static final int DEFAULT_CORE_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int DEFAULT_MAX_POOL_SIZE = Integer.MAX_VALUE;
    private static final int DEFAULT_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final int DEFAULT_KEEP_ALIVE_SECONDS = 60;// S
    private static final String DEFAULT_TASK_GROUP_NAME_PREFIX = "arktask-group-";

    private final AtomicLong taskGroupNumber = new AtomicLong(0);

    private TaskScheduler taskScheduler;

    private TaskEngine(DefaultThreadPoolExecutor executor) {
        this(new DefaultTaskScheduler(executor));
    }

    private TaskEngine(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * 创建无返回结果的任务，任务的执行结果使用回调处理，使用 {@link TaskEngine#commit(Task)} 执行创建的任务
     *
     * @param taskRunner 要执行的具体任务
     * @return 任务
     */
    public Task.Builder buildTask(TaskRunner taskRunner) {
        return new Task.Builder(taskRunner);
    }

    /**
     * 执行指定的任务
     *
     * @param task 要执行的任务，使用 {@link TaskEngine#buildTask(TaskRunner)} 创建
     */
    public void commit(Task task) {
        Assert.notNull(task);

        taskScheduler.submit(task);
    }

    /**
     * 使用默认的任务组名称初始化一个任务组
     *
     * @return {@link TaskGroup}
     */
    public TaskGroup prepareGroup() {
        return this.prepareGroup(DEFAULT_TASK_GROUP_NAME_PREFIX + taskGroupNumber.incrementAndGet());
    }

    /**
     * 初始化一个任务组，可自定义任务组名称
     *
     * @param name 任务组的名称
     * @return {@link TaskGroup}
     */
    public TaskGroup prepareGroup(String name) {
        TaskGroup taskGroup = new TaskGroup(name, this.taskScheduler);
        taskScheduler.addTaskGroup(taskGroup);
        return taskGroup;
    }

    /**
     * 创建有返回结果的任务，使用 {@link ResultTask#get()} 获取任务执行的结果
     *
     * @param executor 要执行的具体任务
     * @param <T>      任务返回结果的具体类型
     * @return 带有返回结果的任务
     */
    public <T> ResultTask.Builder<T> buildResultTask(ResultTaskRunner<T> executor) {
        return new ResultTask.Builder<>(executor);
    }

    /**
     * 直接创建并执行一个默认类型和默认 ID 的有返回结果的任务，使用 {@link ResultTask#get()} 获取任务执行的结果
     *
     * @param executor 要执行的具体任务
     * @param <T>      任务返回结果的具体类型
     * @return 有返回结果的任务
     */
    public <T> ResultTask<T> commit(ResultTaskRunner<T> executor) {
        return this.commit(null, null, executor);
    }

    /**
     * @param type     任务类型
     * @param id       任务 ID
     * @param executor 要执行的具体任务
     * @param <T>      任务返回结果的具体类型
     * @return 有返回结果的任务
     */
    public <T> ResultTask<T> commit(String type, String id, ResultTaskRunner<T> executor) {
        Assert.notNull(executor);

        ResultTask<T> task = new ResultBaseTask<>(type, id, executor);
        this.taskScheduler.submit(task);
        return task;
    }

    /**
     * 判断线程池是否被关闭
     *
     * @return {@code true} 如果线程池已经被关闭
     * @see ThreadPoolExecutor#isShutdown()
     */
    public boolean isShutdown() {
        return this.taskScheduler.isShutdown();
    }

    /**
     * 试图关闭线程池
     *
     * @see ThreadPoolExecutor#shutdown()
     */
    public void shutdown() {
        this.taskScheduler.shutdown();
    }

    /**
     * 试图关闭线程池，同时阻塞等待线程池关闭
     *
     * @param timeout 超时时间
     * @param unit    超时时间的时间单位
     * @return {@code true} 线程池正常关闭；如果到达超时时间后，线程池仍未关闭，返回 {@code false}
     * @see ThreadPoolExecutor#shutdown()
     * @see ThreadPoolExecutor#awaitTermination(long, TimeUnit)
     * @since 1.2.0
     */
    public boolean shutdown(long timeout, TimeUnit unit) throws InterruptedException {
        this.taskScheduler.shutdown();
        return this.taskScheduler.awaitTermination(timeout, unit);
    }

    /**
     * 立即关闭线程池
     *
     * @see ThreadPoolExecutor#shutdownNow()
     * @since 1.2.0
     */
    public void shutdownNow() {
        this.taskScheduler.shutdownNow();
    }

    public List<Task> getRunningTasks() {
        return taskScheduler.getRunningTasks();
    }

    public int getRunningNumberofTask() {
        return this.taskScheduler.getRunningNumberofTask();
    }

    // 获取已经完成的任务总量，包含任务组中的任务
    public long getCompletedNumberOfTask() {
        return this.taskScheduler.getCompletedNumberOfTask();
    }

    // 获取执行的任务总量
    public long getTotalNumberOfTask() {
        return this.taskScheduler.getTotalNumberOfTask();
    }

    public Collection<TaskGroup> getRunningTaskGroups() {
        return this.taskScheduler.getRunningTaskGroups();
    }

    public void start() {
        this.taskScheduler.start();
    }

    public static class Builder {

        private int coreSize = DEFAULT_CORE_SIZE;
        private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
        private int queueCapacity = DEFAULT_QUEUE_CAPACITY;
        private int keepAliveSeconds = DEFAULT_KEEP_ALIVE_SECONDS;
        private RejectedExecutionHandler rejectedExecutionHandler;
        private CompletedTaskHandler completedTaskHandler;
        private boolean windowsScheduledExecutor;

        public Builder corePoolSize(int corePoolSize) {
            if (corePoolSize > 0) {
                this.coreSize = corePoolSize;
            }
            return this;
        }

        public Builder maxPoolSize(int maxPoolSize) {
            if (maxPoolSize > 0) {
                this.maxPoolSize = maxPoolSize;
            }
            return this;
        }

        public Builder keepAliveSeconds(int keepAliveSeconds) {
            if (keepAliveSeconds >= 0) {
                this.keepAliveSeconds = keepAliveSeconds;
            }
            return this;
        }

        public Builder queueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
            return this;
        }

        public Builder rejectedExecutionHandler(RejectedExecutionHandler handler) {
            Assert.notNull(handler);

            this.rejectedExecutionHandler = handler;
            return this;
        }

//        public Builder completedTaskHandler(CompletedTaskHandler handler) {
//            this.completedTaskHandler = handler;
//            return this;
//        }

        public Builder windowsScheduledExecutor() {
            this.windowsScheduledExecutor = true;
            return this;
        }

        public TaskEngine build() {
            BlockingQueue<Runnable> queue = createQueue(this.queueCapacity);
            DefaultThreadPoolExecutor executor = new DefaultThreadPoolExecutor(this.coreSize, this.maxPoolSize,
                this.keepAliveSeconds, TimeUnit.SECONDS, queue, Executors.defaultThreadFactory(),
                getRejectedExecutionHandler(this.rejectedExecutionHandler), this.completedTaskHandler);
            if(windowsScheduledExecutor) {
                return new TaskEngine(new WindowTaskScheduler(executor));
            }
            return new TaskEngine(executor);
        }

        private BlockingQueue<Runnable> createQueue(int queueCapacity) {
            if (queueCapacity > 0) {
                return new LinkedBlockingQueue<>(queueCapacity);
            }
            return new SynchronousQueue<>();
        }

        private RejectedExecutionHandler getRejectedExecutionHandler(RejectedExecutionHandler handler) {
            if (handler != null) {
                return handler;
            }
            return new ThreadPoolExecutor.AbortPolicy();
        }

    }

}
