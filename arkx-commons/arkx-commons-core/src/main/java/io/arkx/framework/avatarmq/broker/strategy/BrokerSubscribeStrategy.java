package io.arkx.framework.avatarmq.broker.strategy;

import io.arkx.framework.avatarmq.broker.ConsumerMessageListener;
import io.arkx.framework.avatarmq.broker.ProducerMessageListener;
import io.arkx.framework.avatarmq.model.MessageType;
import io.arkx.framework.avatarmq.model.RemoteChannelData;
import io.arkx.framework.avatarmq.model.RequestMessage;
import io.arkx.framework.avatarmq.model.ResponseMessage;
import io.arkx.framework.avatarmq.msg.SubscribeMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @filename:BrokerSubscribeStrategy.java
 * @description:BrokerSubscribeStrategy功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class BrokerSubscribeStrategy implements BrokerStrategy {

    private ConsumerMessageListener hookConsumer;

    private ChannelHandlerContext channelHandler;

    public BrokerSubscribeStrategy() {

    }

    public void messageDispatch(RequestMessage request, ResponseMessage response) {
        SubscribeMessage subcript = (SubscribeMessage) request.getMsgParams();
        String clientKey = subcript.getConsumerId();
        RemoteChannelData channel = new RemoteChannelData(channelHandler.channel(), clientKey);
        hookConsumer.hookConsumerMessage(subcript, channel);
        response.setMsgType(MessageType.AvatarMQConsumerAck);
        channelHandler.writeAndFlush(response);
    }

    public void setHookConsumer(ConsumerMessageListener hookConsumer) {
        this.hookConsumer = hookConsumer;
    }

    public void setChannelHandler(ChannelHandlerContext channelHandler) {
        this.channelHandler = channelHandler;
    }

    public void setHookProducer(ProducerMessageListener hookProducer) {

    }

}
