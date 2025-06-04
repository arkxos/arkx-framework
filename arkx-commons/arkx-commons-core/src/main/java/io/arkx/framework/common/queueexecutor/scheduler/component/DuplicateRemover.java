package io.arkx.framework.common.queueexecutor.scheduler.component;

import io.arkx.framework.common.queueexecutor.Element;

/**
 * Remove duplicate requests.
 * @author Darkness
 * @date 2015-1-9 下午10:44:11
 * @version V1.0
 * @since infinity 1.0
 */
public interface DuplicateRemover<T> {
	/**
	 * 
	 * Check whether the request is duplicate.
	 * 
	 * @param element
	 * @return
	 */
	boolean isDuplicate(Element<T> element);

	/**
	 * Reset duplicate check.
	 *
	 */
	void resetDuplicateCheck();

	/**
	 * Get TotalRequestsCount for monitor.
	 *
	 * @return
	 */
	int getTotalElementsCount();
}
