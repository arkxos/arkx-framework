package io.arkx.framework.commons.simplequeue.scheduler;

import io.arkx.framework.commons.simplequeue.ElementWarpper;
import io.arkx.framework.commons.simplequeue.Task;
import io.arkx.framework.commons.simplequeue.scheduler.component.DuplicateRemover;
import io.arkx.framework.commons.simplequeue.scheduler.component.HashSetDuplicateRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remove duplicate urls and only push urls which are not duplicate.<br><br>
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class DuplicateRemovedScheduler implements Scheduler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();

    public DuplicateRemover getDuplicateRemover() {
        return duplicatedRemover;
    }

    public DuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicatedRemover) {
        this.duplicatedRemover = duplicatedRemover;
        return this;
    }

    @Override
    public void push(ElementWarpper request, Task task) {
        logger.trace("get a candidate url {}", request.get());
        if (shouldReserved(request) || noNeedToRemoveDuplicate(request) || !duplicatedRemover.isDuplicate(request, task)) {
            logger.debug("push to queue {}", request.get());
            pushWhenNoDuplicate(request, task);
        }
    }

    protected boolean shouldReserved(ElementWarpper request) {
        return request.getExtra(ElementWarpper.CYCLE_TRIED_TIMES) != null;
    }

    protected boolean noNeedToRemoveDuplicate(ElementWarpper request) {
        return false;
    }

    protected void pushWhenNoDuplicate(ElementWarpper request, Task task) {

    }
}
