package com.arkxos.framework.message.tcp.struct;

import java.util.HashMap;
import java.util.Map;

import com.arkxos.framework.message.tcp.MessageType;

/**
 * 消息头
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:40:03
 * @version 1.0
 * @since 1.0
 */
class Header {

	public static void main(String[] args) {
		System.out.println(new Header().crcCode);
	}
	// 0xABEF + 主版本号  + 次版本号
	private int crcCode = 0xABEF0101;//-1410399999

	private int length;// 消息长度
	private String id;// base58 uuid, 长度 16，两个 long
	private long sessionID;// 会话ID
	private MessageType type;// 消息类型: request/response
	private int businessType;// 业务消息类型
	private byte priority;// 消息优先级
	
	private Map<String, Object> attachment = new HashMap<>(); // 附件
	
	public Header() {
	}
	
	public Header(String id) {
		this.id = id;
	}
	
	public Header(MessageType type, String id) {
		this.type = type;
		this.id = id;
	}
	
	/**
	 * @return the crcCode
	 */
	public final int getCrcCode() {
		return crcCode;
	}

	/**
	 * @param crcCode
	 *            the crcCode to set
	 */
	public final void setCrcCode(int crcCode) {
		this.crcCode = crcCode;
	}

	/**
	 * @return the length
	 */
	public final int getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public final void setLength(int length) {
		this.length = length;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the sessionID
	 */
	public final long getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID
	 *            the sessionID to set
	 */
	public final void setSessionID(long sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * @return the type
	 */
	public final MessageType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public final void setType(MessageType type) {
		this.type = type;
	}
	
	public int getBusinessType() {
		return businessType;
	}

	public void setBusinessType(int businessType) {
		this.businessType = businessType;
	}

	/**
	 * @return the priority
	 */
	public final byte getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public final void setPriority(byte priority) {
		this.priority = priority;
	}

	/**
	 * @return the attachment
	 */
	public final Map<String, Object> getAttachment() {
		return attachment;
	}

	/**
	 * @param attachment
	 *            the attachment to set
	 */
	public final void setAttachment(Map<String, Object> attachment) {
		this.attachment = attachment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Header [crcCode=" + crcCode + ", id=" + id + ", length=" + length + ", sessionID=" + sessionID + ", type=" + type
				+ ", businessType=" + businessType + ", priority=" + priority + ", attachment=" + attachment + "]";
	}
	
}
