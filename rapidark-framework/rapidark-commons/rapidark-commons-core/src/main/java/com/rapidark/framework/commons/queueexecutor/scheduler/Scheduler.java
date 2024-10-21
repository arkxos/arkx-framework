package com.rapidark.framework.commons.queueexecutor.scheduler;

import com.rapidark.framework.commons.queueexecutor.Element;

/**
 * Scheduler is the part of url management.<br>
 * You can implement interface Scheduler to do:
 * manage urls to fetch
 * remove duplicate urls
 * @author Darkness
 * @date 2015-1-9 下午10:35:29
 * @version V1.0
 * @since infinity 1.0
 */
public interface Scheduler<T> {

	/**
	 * add a url to fetch
	 * 
	 * @param task
	 */
	void push(Element<T> element);

	/**
	 * get an url to crawl
	 * 
	 * @param task the task of spider
	 * @return the url to crawl
	 */
	Element<T> poll();

}
