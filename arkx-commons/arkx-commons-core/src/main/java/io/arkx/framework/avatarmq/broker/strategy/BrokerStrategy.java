package io.arkx.framework.avatarmq.broker.strategy;

import io.arkx.framework.avatarmq.broker.ConsumerMessageListener;
import io.arkx.framework.avatarmq.broker.ProducerMessageListener;
import io.arkx.framework.avatarmq.model.RequestMessage;
import io.arkx.framework.avatarmq.model.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @filename:BrokerStrategy.java
 * @description:BrokerStrategy功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface BrokerStrategy {

    void messageDispatch(RequestMessage request, ResponseMessage response);

    void setHookProducer(ProducerMessageListener hookProducer);

    void setHookConsumer(ConsumerMessageListener hookConsumer);

    void setChannelHandler(ChannelHandlerContext channelHandler);
}
