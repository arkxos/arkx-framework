package com.arkxos.framework.commons.download;

import com.arkxos.framework.commons.queueexecutor.Element;
import com.arkxos.framework.commons.queueexecutor.MultiThreadedQueueExecutor;
import com.arkxos.framework.commons.queueexecutor.processor.ElementProcessor;

public class RemoteHttpPartFileProcessor implements ElementProcessor<RemoteHttpPartFile> {

	@Override
	public void process(Element<RemoteHttpPartFile> element,
			MultiThreadedQueueExecutor<RemoteHttpPartFile> executor) {
		RemoteHttpPartFile remoteHttpPartFile = element.getSource();
		if(!remoteHttpPartFile.isActive()) {
			return;
		}
		
		remoteHttpPartFile.download();
	}
	
	
}