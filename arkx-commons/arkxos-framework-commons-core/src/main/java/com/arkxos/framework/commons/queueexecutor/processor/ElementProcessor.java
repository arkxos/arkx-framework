package com.arkxos.framework.commons.queueexecutor.processor;

import com.arkxos.framework.commons.queueexecutor.Element;
import com.arkxos.framework.commons.queueexecutor.MultiThreadedQueueExecutor;

/**   
 * 
 * @author Darkness
 * @date 2015-1-9 下午3:08:23 
 * @version V1.0   
 */
public interface ElementProcessor<T> {

	void process(Element<T> element, MultiThreadedQueueExecutor<T> executor);
	
}
