package io.arkx.framework.avatarmq.core;

import io.arkx.framework.avatarmq.model.MessageDispatchTask;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @filename:MessageTaskQueue.java
 * @description:MessageTaskQueue功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class MessageTaskQueue {

    private static AtomicBoolean isInit = new AtomicBoolean(false);
    private static ConcurrentLinkedQueue<MessageDispatchTask> taskQueue = null;

    private volatile static MessageTaskQueue task = null;

    private MessageTaskQueue() {
    }

    public static MessageTaskQueue getInstance() {
        if (isInit.compareAndSet(false, true)) {
            taskQueue = new ConcurrentLinkedQueue<MessageDispatchTask>();
            task = new MessageTaskQueue();
        }
        return task;
    }

    public boolean pushTask(MessageDispatchTask task) {
        return taskQueue.offer(task);
    }

    public boolean pushTask(List<MessageDispatchTask> tasks) {
        return taskQueue.addAll(tasks);
    }

    public MessageDispatchTask getTask() {
        return taskQueue.poll();
    }
}
