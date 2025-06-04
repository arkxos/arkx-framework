package com.arkxos.framework.message.mq;

import com.arkxos.framework.message.tcp.struct.NettyMessage;

public interface MessageHandler {

	void handle(NettyMessage message);
	
}
