package io.arkx.framework.avatarmq.broker.strategy;

import io.arkx.framework.avatarmq.broker.ConsumerMessageListener;
import io.arkx.framework.avatarmq.broker.ProducerMessageListener;
import io.arkx.framework.avatarmq.model.MessageSource;
import io.arkx.framework.avatarmq.model.RequestMessage;
import io.arkx.framework.avatarmq.model.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @filename:BrokerStrategyContext.java
 * @description:BrokerStrategyContext功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class BrokerStrategyContext {

    public final static int AvatarMQProducerMessageStrategy = 1;
    public final static int AvatarMQConsumerMessageStrategy = 2;
    public final static int AvatarMQSubscribeStrategy = 3;
    public final static int AvatarMQUnsubscribeStrategy = 4;

    private RequestMessage request;
    private ResponseMessage response;
    private ChannelHandlerContext channelHandler;
    private ProducerMessageListener hookProducer;
    private ConsumerMessageListener hookConsumer;
    private BrokerStrategy strategy;

    private static Map<Integer,BrokerStrategy> strategyMap = new HashMap<>();

    static {
        strategyMap.put(AvatarMQProducerMessageStrategy, new BrokerProducerMessageStrategy());
        strategyMap.put(AvatarMQConsumerMessageStrategy, new BrokerConsumerMessageStrategy());
        strategyMap.put(AvatarMQSubscribeStrategy, new BrokerSubscribeStrategy());
        strategyMap.put(AvatarMQUnsubscribeStrategy, new BrokerUnsubscribeStrategy());
    }

    public BrokerStrategyContext(RequestMessage request, ResponseMessage response, ChannelHandlerContext channelHandler) {
        this.request = request;
        this.response = response;
        this.channelHandler = channelHandler;
    }

    public void setHookProducer(ProducerMessageListener hookProducer) {
        this.hookProducer = hookProducer;
    }

    public void setHookConsumer(ConsumerMessageListener hookConsumer) {
        this.hookConsumer = hookConsumer;
    }

    public void invoke() {
        switch (request.getMsgType()) {
            case AvatarMQMessage:
                strategy = strategyMap.get(request.getMsgSource() == MessageSource.AvatarMQProducer ? AvatarMQProducerMessageStrategy : AvatarMQConsumerMessageStrategy);
                break;
            case AvatarMQSubscribe:
                strategy = strategyMap.get(AvatarMQSubscribeStrategy);
                break;
            case AvatarMQUnsubscribe:
                strategy = strategyMap.get(AvatarMQUnsubscribeStrategy);
                break;
            default:
                break;
        }

        strategy.setChannelHandler(channelHandler);
        strategy.setHookConsumer(hookConsumer);
        strategy.setHookProducer(hookProducer);
        strategy.messageDispatch(request, response);
    }
}
