package io.arkx.framework.commons.crawl.scheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.annotation.ThreadSafe;

import io.arkx.framework.commons.crawl.Request;

/**   
 * 
 * @author Darkness
 * @date 2015-1-9 下午5:18:38 
 * @version V1.0   
 */
@ThreadSafe
public class QueueScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

    private BlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();

    @Override
    public void pushWhenNoDuplicate(Request request) {
        queue.add(request);
    }

    @Override
    public synchronized Request poll() {
        return queue.poll();
    }

    @Override
    public int getLeftRequestsCount() {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount() {
        return getDuplicateRemover().getTotalRequestsCount();
    }
}
