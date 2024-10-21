package com.rapidark.framework.message.tcp.struct;

import com.rapidark.framework.message.tcp.MessageType;

public class RequestMessage extends NettyMessage {

	public RequestMessage(String id) {
		super(MessageType.REQUEST, id);
	}

}
