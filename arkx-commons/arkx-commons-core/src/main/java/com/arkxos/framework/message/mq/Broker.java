package com.arkxos.framework.message.mq;

import java.util.Collection;

import com.arkxos.framework.commons.util.UuidUtil;
import com.arkxos.framework.message.tcp.server.NettyServer;
import com.arkxos.framework.message.tcp.struct.RequestMessage;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import io.netty.channel.Channel;

public class Broker extends NettyServer {

	private Multimap<String, Channel> producers = ArrayListMultimap.create();
	private Multimap<String, Channel> consumers = ArrayListMultimap.create();
	
	public Broker(String serverAddress) {
		super(serverAddress);
	}
	
	@Override
	public void start() throws Exception {
		registerChannelHandler(new TopicMessageHandler(this));
		super.start();
	}
	
	public void registerProducer(String topic, Channel channel) {
		synchronized (this) {
			producers.put(topic, channel);
		}
	}
	
	public void registerConsumer(String topic, Channel channel) {
		synchronized (this) {
			consumers.put(topic, channel);
		}
	}

	public void publish(String topic, String data) {
		Collection<Channel> topicConsumers = consumers.get(topic);
		for (Channel channel : topicConsumers) {
			RequestMessage message = new RequestMessage(UuidUtil.base58Uuid());
			message.setBusinessType(MqBusinessType.TopicMessage.value());
			message.setBody(data.getBytes());
			channel.writeAndFlush(message);
		}
	}
	
}
