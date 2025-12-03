package io.arkx.framework.message.tcp.struct;

import io.arkx.framework.message.tcp.MessageType;

public class RequestMessage extends NettyMessage {

    public RequestMessage(String id) {
        super(MessageType.REQUEST, id);
    }

}
