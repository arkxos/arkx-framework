package io.arkx.framework.commons.queueexecutor.scheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.annotation.ThreadSafe;

import io.arkx.framework.commons.queueexecutor.Element;

/**
 *
 * @author Darkness
 * @date 2015-1-9 下午5:18:38
 * @version V1.0
 */
@ThreadSafe
public class QueueScheduler<T> extends DuplicateRemovedScheduler<T> implements MonitorableScheduler<T> {

    private BlockingQueue<Element<T>> queue = new LinkedBlockingQueue<Element<T>>();

    @Override
    public void pushWhenNoDuplicate(Element<T> task) {
        queue.add(task);
    }

    @Override
    public synchronized Element<T> poll() {
        return queue.poll();
    }

    @Override
    public int getLeftElementsCount() {
        return queue.size();
    }

    @Override
    public int getTotalElementsCount() {
        return getDuplicateRemover().getTotalElementsCount();
    }
}
