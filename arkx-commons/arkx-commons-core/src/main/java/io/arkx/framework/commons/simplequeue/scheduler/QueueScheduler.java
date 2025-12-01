package io.arkx.framework.commons.simplequeue.scheduler;

import io.arkx.framework.commons.simplequeue.ElementWarpper;
import io.arkx.framework.commons.simplequeue.Task;
import org.apache.http.annotation.ThreadSafe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Basic Scheduler implementation.<br>
 * Store urls to fetch in LinkedBlockingQueue and remove duplicate urls by HashMap.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class QueueScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

    private BlockingQueue<ElementWarpper> queue = new LinkedBlockingQueue<ElementWarpper>();

    @Override
    public void pushWhenNoDuplicate(ElementWarpper request, Task task) {
        queue.add(request);
    }

    @Override
    public ElementWarpper poll(Task task) {
        return queue.poll();
    }

    @Override
    public int getLeftElementsCount(Task task) {
        return queue.size();
    }

    @Override
    public int getTotalElementsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}
