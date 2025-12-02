package io.arkx.framework.message.tcp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.arkx.framework.commons.util.UuidUtil;
import io.arkx.framework.message.tcp.client.NettyClient;
import io.arkx.framework.message.tcp.struct.NettyMessage;
import io.netty.channel.Channel;

public class MessageProcessor {

	private Map<String, MessageSender> sendersMap = new ConcurrentHashMap<>();

	private long lastRecivedServerMessageTime = System.nanoTime();

	private Channel channel;

	private NettyClient client;

	public MessageProcessor(NettyClient client, Channel channel) {
		this.client = client;
		this.channel = channel;
	}

	public NettyMessage send(NettyMessage message) {
		MessageSender sender = new MessageSender(channel);
		if (message.getId() == null) {
			message.setId(UuidUtil.base58Uuid());
		}
		sendersMap.put(message.getId(), sender);
		return sender.send(message);
	}

	public void setResponseMessage(NettyMessage message) {
		String id = message.getId();
		MessageSender sender = sendersMap.get(id);
		if (sender != null) {
			sender.setMessageResult(message);
			sendersMap.remove(id);
		}
	}

	public void onMessage(NettyMessage message) {
		lastRecivedServerMessageTime = System.nanoTime();

		if (message.getType() == MessageType.RESPONSE) {
			setResponseMessage(message);
		}

		// for (ClientMessageHandler messageHandler : client.getMessageHandlers()) {
		// messageHandler.handle(message);
		// }
	}

	public long getLastRecivedServerMessageTime() {
		return lastRecivedServerMessageTime;
	}

}
