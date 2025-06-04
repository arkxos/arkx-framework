package com.arkxos.framework.message.tcp.struct;

import com.arkxos.framework.message.tcp.MessageType;

public class ResponseMessage extends NettyMessage {

	public ResponseMessage(String id) {
		super(MessageType.RESPONSE, id);
	}

}
