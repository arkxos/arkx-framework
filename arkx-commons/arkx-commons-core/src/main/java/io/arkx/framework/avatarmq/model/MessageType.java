package io.arkx.framework.avatarmq.model;

/**
 * @filename:MessageType.java
 * @description:MessageType功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public enum MessageType {

	AvatarMQSubscribe(1), AvatarMQUnsubscribe(2), AvatarMQMessage(3), AvatarMQProducerAck(4), AvatarMQConsumerAck(5);

	private int messageType;

	private MessageType(int messageType) {
		this.messageType = messageType;
	}

	int getMessageType() {
		return messageType;
	}

}
