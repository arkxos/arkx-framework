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

public class Producer extends NettyClient {

	private Logger logger = LoggerFactory.getLogger(Producer.class);
	
	private String topic;
	
	public Producer(String serverAddress, String topic) {
		super(serverAddress);
		this.topic = topic;
	}
	
	@Override
	public void start() throws Exception {
		super.start();
		
		RequestMessage message = new RequestMessage(UuidUtil.base58Uuid());
		message.setBusinessType(MqBusinessType.RegisterProducer.value());
		
		Map<String, String> data = new HashMap<>();
		data.put("topic", topic);
		message.setBody(JSON.toJSONString(data).getBytes());
		
		NettyMessage responseMessage = sendMessage(message);
		if (responseMessage != null) {
			logger.info("register producer on topic[" + topic + "] success");
		}
	}

	public void sendMessage(String topicMessage) {
		RequestMessage message = new RequestMessage(UuidUtil.base58Uuid());
		message.setBusinessType(MqBusinessType.TopicMessage.value());
		
		Map<String, String> data = new HashMap<>();
		data.put("topic", topic);
		data.put("msg", topicMessage);
		message.setBody(JSON.toJSONString(data).getBytes());
		
		sendMessage(message);
	}

}
