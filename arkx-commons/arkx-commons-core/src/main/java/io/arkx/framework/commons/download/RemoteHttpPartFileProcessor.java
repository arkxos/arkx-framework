package io.arkx.framework.commons.download;

import io.arkx.framework.commons.queueexecutor.Element;
import io.arkx.framework.commons.queueexecutor.MultiThreadedQueueExecutor;
import io.arkx.framework.commons.queueexecutor.processor.ElementProcessor;

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