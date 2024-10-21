package com.rapidark.framework.commons.download;

import com.rapidark.framework.commons.queueexecutor.Element;
import com.rapidark.framework.commons.queueexecutor.MultiThreadedQueueExecutor;
import com.rapidark.framework.commons.queueexecutor.processor.ElementProcessor;

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