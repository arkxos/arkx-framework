package com.arkxos.framework.common.queueexecutor.processor;

import com.arkxos.framework.common.queueexecutor.Element;
import com.arkxos.framework.common.queueexecutor.MultiThreadedQueueExecutor;

/**   
 * 
 * @author Darkness
 * @date 2015-1-9 下午3:08:23 
 * @version V1.0   
 */
public interface ElementProcessor<T> {

	void process(Element<T> element, MultiThreadedQueueExecutor<T> executor);
	
}
