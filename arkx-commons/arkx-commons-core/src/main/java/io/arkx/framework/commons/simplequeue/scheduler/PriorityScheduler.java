package io.arkx.framework.commons.simplequeue.scheduler;

import io.arkx.framework.commons.simplequeue.ElementWarpper;
import io.arkx.framework.commons.simplequeue.Task;
import io.arkx.framework.commons.simplequeue.utils.NumberUtils;
import org.apache.http.annotation.ThreadSafe;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Priority scheduler. Request with higher priority will poll earlier. <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
@ThreadSafe
public class PriorityScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

    public static final int INITIAL_CAPACITY = 5;

    private BlockingQueue<ElementWarpper> noPriorityQueue = new LinkedBlockingQueue<ElementWarpper>();

    private PriorityBlockingQueue<ElementWarpper> priorityQueuePlus = new PriorityBlockingQueue<ElementWarpper>(INITIAL_CAPACITY, new Comparator<ElementWarpper>() {
        @Override
        public int compare(ElementWarpper o1, ElementWarpper o2) {
            return -NumberUtils.compareLong(o1.getPriority(), o2.getPriority());
        }
    });

    private PriorityBlockingQueue<ElementWarpper> priorityQueueMinus = new PriorityBlockingQueue<ElementWarpper>(INITIAL_CAPACITY, new Comparator<ElementWarpper>() {
        @Override
        public int compare(ElementWarpper o1, ElementWarpper o2) {
            return -NumberUtils.compareLong(o1.getPriority(), o2.getPriority());
        }
    });

    @Override
    public void pushWhenNoDuplicate(ElementWarpper request, Task task) {
        if (request.getPriority() == 0) {
            noPriorityQueue.add(request);
        } else if (request.getPriority() > 0) {
            priorityQueuePlus.put(request);
        } else {
            priorityQueueMinus.put(request);
        }
    }

    @Override
    public synchronized ElementWarpper poll(Task task) {
        ElementWarpper poll = priorityQueuePlus.poll();
        if (poll != null) {
            return poll;
        }
        poll = noPriorityQueue.poll();
        if (poll != null) {
            return poll;
        }
        return priorityQueueMinus.poll();
    }

    @Override
    public int getLeftElementsCount(Task task) {
        return noPriorityQueue.size();
    }

    @Override
    public int getTotalElementsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}
