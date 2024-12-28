package com.rapidark.framework.commons.simplequeue;

/**
 * Interface for identifying different tasks.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 * @see com.rapidark.framework.commons.simplequeue.scheduler.Scheduler
 * @see com.rapidark.framework.commons.simplequeue.pipeline.Pipeline
 */
public interface Task {

    /**
     * unique id for a task.
     *
     * @return uuid
     */
    public String getUUID();

    /**
     * site of a task
     *
     * @return site
     */
    public Config getSite();

}
