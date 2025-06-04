package com.arkxos.framework.message.mq;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import io.arkx.framework.commons.util.UuidUtil;
import com.arkxos.framework.message.tcp.client.NettyClient;
import com.arkxos.framework.message.tcp.struct.NettyMessage;
import com.arkxos.framework.message.tcp.struct.RequestMessage;

public class Consumer extends NettyClient {

	private Logger logger = LoggerFactory.getLogger(Consumer.class);
	
	private String topic;
	MessageHandler handler;
	
	public Consumer(String serverAddress, String topic, MessageHandler handler) {
		super(serverAddress);
		this.topic = topic;
		this.handler = handler;
	}
	
	@Override
	public void start() throws Exception {
		super.start();
		
		NettyMessage message = new RequestMessage(UuidUtil.base58Uuid());
		message.setBusinessType(MqBusinessType.RegisterConsumer.value());
		
		Map<String, String> data = new HashMap<>();
		data.put("topic", topic);
		message.setBody(JSON.toJSONString(data).getBytes());
		
		NettyMessage responseMessage = sendMessage(message);
		if (responseMessage != null) {
			logger.info("register Consumer on topic[" + topic + "] success");
		}
	}
	
	@Override
	public void onMessage(NettyMessage message) {
		super.onMessage(message);
		if(message.getBusinessType() == MqBusinessType.TopicMessage.value()) {
			handler.handle(message);
		}
	}
	
}