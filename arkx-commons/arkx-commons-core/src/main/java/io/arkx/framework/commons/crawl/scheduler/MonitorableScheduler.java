package io.arkx.framework.commons.crawl.scheduler;


/**
 * The scheduler whose requests can be counted for monitor.
 * 
 * @author Darkness
 * @date 2015-1-9 下午10:37:12
 * @version V1.0
 * @since infinity 1.0
 */
public interface MonitorableScheduler extends Scheduler {

	int getLeftRequestsCount();

	int getTotalRequestsCount();

}
