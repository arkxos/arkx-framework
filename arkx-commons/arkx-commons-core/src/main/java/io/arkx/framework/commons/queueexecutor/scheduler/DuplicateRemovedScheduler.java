package io.arkx.framework.commons.queueexecutor.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.arkx.framework.commons.queueexecutor.Element;
import io.arkx.framework.commons.queueexecutor.scheduler.component.DuplicateRemover;
import io.arkx.framework.commons.queueexecutor.scheduler.component.HashSetDuplicateRemover;

/**
 * Remove duplicate urls and only push urls which are not duplicate.<br>
 * </br>
 *
 * @author Darkness
 * @date 2015-1-9 下午10:38:41
 * @version V1.0
 * @since infinity 1.0
 */
public abstract class DuplicateRemovedScheduler<T> implements Scheduler<T> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DuplicateRemover<T> duplicatedRemover = new HashSetDuplicateRemover<T>();

    public DuplicateRemover<T> getDuplicateRemover() {
        return duplicatedRemover;
    }

    public DuplicateRemovedScheduler<T> setDuplicateRemover(DuplicateRemover<T> duplicatedRemover) {
        this.duplicatedRemover = duplicatedRemover;
        return this;
    }

    @Override
    public void push(Element<T> element) {
        logger.trace("get a candidate element {}", element.getId());
        if (!duplicatedRemover.isDuplicate(element) || shouldReserved(element)) {
            logger.debug("push to queue {}", element.getId());
            pushWhenNoDuplicate(element);
        }
    }

    protected boolean shouldReserved(Element<T> element) {
        return element.getExtra(Element.CYCLE_TRIED_TIMES) != null;
    }

    protected void pushWhenNoDuplicate(Element<T> element) {

    }
}
