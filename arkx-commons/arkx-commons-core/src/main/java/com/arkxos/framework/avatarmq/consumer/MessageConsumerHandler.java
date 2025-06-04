package com.arkxos.framework.avatarmq.consumer;

import com.arkxos.framework.avatarmq.core.HookMessageEvent;
import com.arkxos.framework.avatarmq.model.MessageSource;
import com.arkxos.framework.avatarmq.model.MessageType;
import com.arkxos.framework.avatarmq.model.RequestMessage;
import com.arkxos.framework.avatarmq.model.ResponseMessage;
import com.arkxos.framework.avatarmq.msg.ConsumerAckMessage;
import com.arkxos.framework.avatarmq.netty.MessageEventWrapper;
import com.arkxos.framework.avatarmq.netty.MessageProcessor;

import io.netty.channel.ChannelHandlerContext;

/**
 * @filename:MessageConsumerHandler.java
 * @description:MessageConsumerHandler功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class MessageConsumerHandler extends MessageEventWrapper<Object> {

    private String key;

    public MessageConsumerHandler(MessageProcessor processor) {
        this(processor, null);
        super.setWrapper(this);
    }

    public MessageConsumerHandler(MessageProcessor processor, HookMessageEvent hook) {
        super(processor, hook);
        super.setWrapper(this);
    }

    @Override
    public void beforeMessage(Object msg) {
        key = ((ResponseMessage) msg).getMsgId();
    }

    @Override
    public void handleMessage(ChannelHandlerContext ctx, Object msg) {
        if (!factory.traceInvoker(key) && hook != null) {

            ResponseMessage message = (ResponseMessage) msg;
            ConsumerAckMessage result = (ConsumerAckMessage) hook.callBackMessage(message);
            if (result != null) {
                RequestMessage request = new RequestMessage();
                request.setMsgId(message.getMsgId());
                request.setMsgSource(MessageSource.AvatarMQConsumer);
                request.setMsgType(MessageType.AvatarMQMessage);
                request.setMsgParams(result);

                ctx.writeAndFlush(request);
            }
        }
    }
}
