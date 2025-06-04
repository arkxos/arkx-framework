package org.ark.framework.messages;

import io.arkx.framework.commons.collection.Mapx;

/**
 * @class org.ark.framework.messages.MessageReceiver
 * 
 * @author Darkness
 * @date 2013-1-31 上午11:30:54 
 * @version V1.0
 */
public abstract class MessageReceiver {
	public String[] getMessageTypeNames() {
		return MessageBus.getMessageNames(this);
	}

	public abstract Mapx<String, Object> receive(Message paramMessage);
}