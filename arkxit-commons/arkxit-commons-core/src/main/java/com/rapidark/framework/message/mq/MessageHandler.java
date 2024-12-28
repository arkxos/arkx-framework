package com.rapidark.framework.message.mq;

import com.rapidark.framework.message.tcp.struct.NettyMessage;

public interface MessageHandler {

	void handle(NettyMessage message);
	
}
