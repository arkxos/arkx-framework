package io.arkx.framework.commons.queueexecutor.scheduler;

/**
 * The scheduler whose requests can be counted for monitor.
 *
 * @author Darkness
 * @date 2015-1-9 下午10:37:12
 * @version V1.0
 * @since infinity 1.0
 */
public interface MonitorableScheduler<T> extends Scheduler<T> {

	int getLeftElementsCount();

	int getTotalElementsCount();

}
