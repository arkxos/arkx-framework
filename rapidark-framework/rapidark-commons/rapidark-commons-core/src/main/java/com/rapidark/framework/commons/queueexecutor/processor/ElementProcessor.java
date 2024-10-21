package com.rapidark.framework.commons.queueexecutor.processor;

import com.rapidark.framework.commons.queueexecutor.Element;
import com.rapidark.framework.commons.queueexecutor.MultiThreadedQueueExecutor;

/**   
 * 
 * @author Darkness
 * @date 2015-1-9 下午3:08:23 
 * @version V1.0   
 */
public interface ElementProcessor<T> {

	void process(Element<T> element, MultiThreadedQueueExecutor<T> executor);
	
}
