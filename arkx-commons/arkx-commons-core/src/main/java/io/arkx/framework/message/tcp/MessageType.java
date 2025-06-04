package io.arkx.framework.message.tcp;

/**
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:38:08
 * @version 1.0
 * @since 1.0
 */
public enum MessageType {

	REQUEST((byte) 0), 
	RESPONSE((byte) 1);
	
	private byte value;

	private MessageType(byte value) {
		this.value = value;
	}

	public byte value() {
		return this.value;
	}

	public static MessageType valueOf(byte value) {
		for (MessageType messageType : MessageType.values()) {
			if(messageType.value == value) {
				return messageType;
			}
		}
		return null;
	}
}
