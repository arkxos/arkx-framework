package io.arkx.framework.commons.queueexecutor.processor;

import io.arkx.framework.commons.queueexecutor.Element;
import io.arkx.framework.commons.queueexecutor.MultiThreadedQueueExecutor;

/**   
 * 
 * @author Darkness
 * @date 2015-1-9 下午3:08:23 
 * @version V1.0   
 */
public interface ElementProcessor<T> {

	void process(Element<T> element, MultiThreadedQueueExecutor<T> executor);
	
}
