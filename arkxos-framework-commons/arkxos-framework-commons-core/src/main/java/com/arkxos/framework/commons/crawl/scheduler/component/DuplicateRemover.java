package com.arkxos.framework.commons.crawl.scheduler.component;

import com.arkxos.framework.commons.crawl.Request;

/**
 * Remove duplicate requests.
 * @author Darkness
 * @date 2015-1-9 下午10:44:11
 * @version V1.0
 * @since infinity 1.0
 */
public interface DuplicateRemover {
	/**
	 * 
	 * Check whether the request is duplicate.
	 * 
	 * @param request
	 * @param task
	 * @return
	 */
	boolean isDuplicate(Request request);

	/**
	 * Reset duplicate check.
	 * 
	 * @param task
	 */
	void resetDuplicateCheck();

	/**
	 * Get TotalRequestsCount for monitor.
	 * 
	 * @param task
	 * @return
	 */
	int getTotalRequestsCount();
}
