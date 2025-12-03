package io.arkx.framework.commons.simplequeue;

import io.arkx.framework.commons.simplequeue.pipeline.Pipeline;
import io.arkx.framework.commons.simplequeue.scheduler.Scheduler;

/**
 * Interface for identifying different tasks.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 * @see Scheduler
 * @see Pipeline
 */
public interface Task {

    /**
     * unique id for a task.
     *
     * @return uuid
     */
    String getUUID();

    /**
     * site of a task
     *
     * @return site
     */
    Config getSite();

}
