package com.rapidark.common.queueexecutor.processor;

import com.rapidark.common.queueexecutor.Element;
import com.rapidark.common.queueexecutor.MultiThreadedQueueExecutor;

/**   
 * 
 * @author Darkness
 * @date 2015-1-9 下午3:08:23 
 * @version V1.0   
 */
public interface ElementProcessor<T> {

	void process(Element<T> element, MultiThreadedQueueExecutor<T> executor);
	
}
