package io.arkx.framework.avatarmq.broker;

import java.util.concurrent.atomic.AtomicReference;

import io.arkx.framework.avatarmq.broker.strategy.BrokerStrategyContext;
import io.arkx.framework.avatarmq.model.MessageSource;
import io.arkx.framework.avatarmq.model.RequestMessage;
import io.arkx.framework.avatarmq.model.ResponseMessage;
import io.arkx.framework.avatarmq.netty.ShareMessageEventWrapper;
import io.netty.channel.ChannelHandlerContext;

/**
 * @filename:MessageBrokerHandler.java
 * @description:MessageBrokerHandler功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class MessageBrokerHandler extends ShareMessageEventWrapper<Object> {

	private AtomicReference<ProducerMessageListener> hookProducer;

	private AtomicReference<ConsumerMessageListener> hookConsumer;

	private AtomicReference<RequestMessage> message = new AtomicReference<>();

	public MessageBrokerHandler() {
		super.setWrapper(this);
	}

	public MessageBrokerHandler buildProducerHook(ProducerMessageListener hookProducer) {
		this.hookProducer = new AtomicReference<>(hookProducer);
		return this;
	}

	public MessageBrokerHandler buildConsumerHook(ConsumerMessageListener hookConsumer) {
		this.hookConsumer = new AtomicReference<>(hookConsumer);
		return this;
	}

	@Override
	public void beforeMessage(Object msg) {
		message.set((RequestMessage) msg);
	}

	@Override
	public void handleMessage(ChannelHandlerContext ctx, Object msg) {
		RequestMessage request = message.get();
		ResponseMessage response = new ResponseMessage();
		response.setMsgId(request.getMsgId());
		response.setMsgSource(MessageSource.AvatarMQBroker);

		BrokerStrategyContext strategy = new BrokerStrategyContext(request, response, ctx);
		strategy.setHookConsumer(hookConsumer.get());
		strategy.setHookProducer(hookProducer.get());
		strategy.invoke();
	}

}
