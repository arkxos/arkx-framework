package com.arkxos.framework.avatarmq.broker.strategy;

import com.arkxos.framework.avatarmq.broker.ConsumerMessageListener;
import com.arkxos.framework.avatarmq.broker.ProducerMessageListener;
import com.arkxos.framework.avatarmq.consumer.ConsumerContext;
import com.arkxos.framework.avatarmq.model.RequestMessage;
import com.arkxos.framework.avatarmq.model.ResponseMessage;
import com.arkxos.framework.avatarmq.msg.UnSubscribeMessage;

import io.netty.channel.ChannelHandlerContext;

/**
 * @filename:BrokerUnsubscribeStrategy.java
 * @description:BrokerUnsubscribeStrategy功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class BrokerUnsubscribeStrategy implements BrokerStrategy {

    public BrokerUnsubscribeStrategy() {

    }

    public void messageDispatch(RequestMessage request, ResponseMessage response) {
        UnSubscribeMessage msgUnSubscribe = (UnSubscribeMessage) request.getMsgParams();
        ConsumerContext.unLoad(msgUnSubscribe.getConsumerId());
    }

    public void setHookProducer(ProducerMessageListener hookProducer) {

    }

    public void setHookConsumer(ConsumerMessageListener hookConsumer) {

    }

    public void setChannelHandler(ChannelHandlerContext channelHandler) {

    }
}
