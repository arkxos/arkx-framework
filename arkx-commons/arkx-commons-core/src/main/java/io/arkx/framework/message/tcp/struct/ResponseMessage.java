package io.arkx.framework.message.tcp.struct;

import io.arkx.framework.message.tcp.MessageType;

public class ResponseMessage extends NettyMessage {

	public ResponseMessage(String id) {
		super(MessageType.RESPONSE, id);
	}

}
