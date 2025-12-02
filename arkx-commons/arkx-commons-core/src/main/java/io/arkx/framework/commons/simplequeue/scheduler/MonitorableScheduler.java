package io.arkx.framework.commons.simplequeue.scheduler;

import io.arkx.framework.commons.simplequeue.Task;

/**
 * The scheduler whose requests can be counted for monitor.
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public interface MonitorableScheduler extends Scheduler {

	int getLeftElementsCount(Task task);

	int getTotalElementsCount(Task task);

}
