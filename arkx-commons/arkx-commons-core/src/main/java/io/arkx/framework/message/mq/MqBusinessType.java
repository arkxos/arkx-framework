package io.arkx.framework.message.mq;

/**
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:38:08
 * @version 1.0
 * @since 1.0
 */
public enum MqBusinessType {
	// 0-50为系统保留消息类型
	RegisterProducer(21),
	RegisterConsumer(22),
	TopicMessage(23);

	private int value;

	private MqBusinessType(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}
