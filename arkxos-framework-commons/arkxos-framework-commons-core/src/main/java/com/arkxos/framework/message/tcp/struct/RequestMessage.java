package com.arkxos.framework.message.tcp.struct;

import com.arkxos.framework.message.tcp.MessageType;

public class RequestMessage extends NettyMessage {

	public RequestMessage(String id) {
		super(MessageType.REQUEST, id);
	}

}
