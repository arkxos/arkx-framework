package io.arkx.framework.commons.crawl.scheduler;

import io.arkx.framework.commons.crawl.Request;

/**
 * Scheduler is the part of url management.<br>
 * You can implement interface Scheduler to do: manage urls to fetch remove
 * duplicate urls
 *
 * @author Darkness
 * @date 2015-1-9 下午10:35:29
 * @version V1.0
 * @since infinity 1.0
 */
public interface Scheduler {

    /**
     * add a url to fetch
     *
     * @param request
     */
    void push(Request request);

    /**
     * get an url to crawl
     *
     * @param task
     *            the task of spider
     * @return the url to crawl
     */
    Request poll();

}
