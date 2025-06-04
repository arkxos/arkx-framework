package io.arkx.framework.commons.simplequeue.scheduler.component;

import io.arkx.framework.commons.simplequeue.ElementWarpper;
import io.arkx.framework.commons.simplequeue.Task;

/**
 * Remove duplicate requests.
 * @author code4crafer@gmail.com
 * @since 0.5.1
 */
public interface DuplicateRemover {
    /**
     *
     * Check whether the request is duplicate.
     *
     * @param request request
     * @param task task
     * @return true if is duplicate
     */
    public boolean isDuplicate(ElementWarpper request, Task task);

    /**
     * Reset duplicate check.
     * @param task task
     */
    public void resetDuplicateCheck(Task task);

    /**
     * Get TotalRequestsCount for monitor.
     * @param task task
     * @return number of total request
     */
    public int getTotalRequestsCount(Task task);

}
