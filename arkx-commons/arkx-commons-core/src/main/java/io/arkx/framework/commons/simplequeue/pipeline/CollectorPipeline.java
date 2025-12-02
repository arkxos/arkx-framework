package io.arkx.framework.commons.simplequeue.pipeline;

import java.util.List;

import io.arkx.framework.commons.simplequeue.QueueExecutor;

/**
 * Pipeline that can collect and store results. <br>
 * Used for {@link QueueExecutor#getAll(java.util.Collection)}
 *
 * @author code4crafter@gmail.com
 * @since 0.4.0
 */
public interface CollectorPipeline<T> extends Pipeline {

	/**
	 * Get all results collected.
	 * @return collected results
	 */
	public List<T> getCollected();

}
