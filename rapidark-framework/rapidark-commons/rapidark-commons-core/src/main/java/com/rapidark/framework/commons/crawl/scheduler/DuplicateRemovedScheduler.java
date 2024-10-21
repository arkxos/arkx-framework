package com.rapidark.framework.commons.crawl.scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidark.framework.commons.crawl.Request;
import com.rapidark.framework.commons.crawl.scheduler.component.DuplicateRemover;
import com.rapidark.framework.commons.crawl.scheduler.component.HashSetDuplicateRemover;
/**
 * Remove duplicate urls and only push urls which are not duplicate.<br></br>
 * @author Darkness
 * @date 2015-1-9 下午10:38:41
 * @version V1.0
 * @since infinity 1.0
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
    public void push(Request request) {
        logger.trace("get a candidate url {}", request.getUrl());
        if (!duplicatedRemover.isDuplicate(request) || shouldReserved(request)) {
            logger.debug("push to queue {}", request.getUrl());
            pushWhenNoDuplicate(request);
        }
    }

    protected boolean shouldReserved(Request request) {
        return request.getExtra(Request.CYCLE_TRIED_TIMES) != null;
    }

    protected void pushWhenNoDuplicate(Request request) {

    }
}
