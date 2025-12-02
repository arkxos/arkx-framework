package io.arkx.framework.commons.queueexecutor.scheduler;

import io.arkx.framework.commons.queueexecutor.Element;

/**
 * Scheduler is the part of url management.<br>
 * You can implement interface Scheduler to do: manage urls to fetch remove duplicate urls
 *
 * @author Darkness
 * @date 2015-1-9 下午10:35:29
 * @version V1.0
 * @since infinity 1.0
 */
public interface Scheduler<T> {

	/**
	 * add a url to fetch
	 * @param element
	 */
	void push(Element<T> element);

	/**
	 * get an url to crawl
	 * @return the url to crawl
	 */
	Element<T> poll();

}
