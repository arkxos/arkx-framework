package io.arkx.framework.message.mq;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.arkx.framework.message.tcp.MessageType;
import io.arkx.framework.message.tcp.struct.NettyMessage;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 心跳应答消息处理
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:57:07
 * @version 1.0
 * @since 1.0
 */
@Sharable
public class TopicMessageHandler extends SimpleChannelInboundHandler<NettyMessage> {
	
	private Logger logger = LoggerFactory.getLogger(TopicMessageHandler.class);
	
	Broker broker;
	
	public TopicMessageHandler(Broker broker) {
		this.broker = broker;
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
		if(message == null) {
			ctx.fireChannelRead(message);
			return;
		}
		logger.debug("on recived message, type: " + message.getType() + ", body:" + message.getBody());
		if (message.getBusinessType() == MqBusinessType.RegisterConsumer.value()) {
			String text = new String(message.getBody());
			Map<String, String> msg =  JSON.parseObject(text, new TypeReference<Map<String, String>>(){});
			broker.registerConsumer(msg.get("topic"), ctx.channel());
		} else if (message.getBusinessType() == MqBusinessType.RegisterProducer.value()) {
			String text = new String(message.getBody());
			Map<String, String> msg =  JSON.parseObject(text, new TypeReference<Map<String, String>>(){});
			broker.registerProducer(msg.get("topic"), ctx.channel());
		} else if (message.getType() == MessageType.REQUEST && message.getBusinessType() == MqBusinessType.TopicMessage.value()) {
			String text = new String(message.getBody());
			Map<String, String> msg =  JSON.parseObject(text, new TypeReference<Map<String, String>>(){});
			String topic = msg.get("topic");
			String data = msg.get("msg");
			broker.publish(topic, data);
		} else {
			ctx.fireChannelRead(message);
		}
	}
	
}
