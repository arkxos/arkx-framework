package com.rapidark.framework.avatarmq.broker.strategy;

import com.rapidark.framework.avatarmq.broker.ConsumerMessageListener;
import com.rapidark.framework.avatarmq.broker.ProducerMessageListener;
import com.rapidark.framework.avatarmq.model.RequestMessage;
import com.rapidark.framework.avatarmq.model.ResponseMessage;
import com.rapidark.framework.avatarmq.msg.Message;

import io.netty.channel.ChannelHandlerContext;

/**
 * @filename:BrokerProducerMessageStrategy.java
 * @description:BrokerProducerMessageStrategy功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class BrokerProducerMessageStrategy implements BrokerStrategy {

    private ProducerMessageListener hookProducer;
    private ChannelHandlerContext channelHandler;

    public BrokerProducerMessageStrategy() {
    }

    @Override
    public void messageDispatch(RequestMessage request, ResponseMessage response) {
        Message message = (Message) request.getMsgParams();
        hookProducer.hookProducerMessage(message, request.getMsgId(), channelHandler.channel());
    }

    @Override
    public void setHookProducer(ProducerMessageListener hookProducer) {
        this.hookProducer = hookProducer;
    }

    @Override
    public void setChannelHandler(ChannelHandlerContext channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    public void setHookConsumer(ConsumerMessageListener hookConsumer) {
    }
}
