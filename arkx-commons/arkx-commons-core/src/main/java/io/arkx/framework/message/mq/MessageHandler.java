package io.arkx.framework.message.mq;

import io.arkx.framework.message.tcp.struct.NettyMessage;

public interface MessageHandler {

    void handle(NettyMessage message);

}
