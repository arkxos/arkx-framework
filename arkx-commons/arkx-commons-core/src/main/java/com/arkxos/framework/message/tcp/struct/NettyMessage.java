package com.arkxos.framework.message.tcp.struct;

import java.util.Map;

import io.arkx.framework.commons.util.ByteUtil;
import com.arkxos.framework.message.tcp.MessageType;

/**
 * 消息
 * @author Darkness
 * @date 2017年4月11日 下午3:38:55
 * @version 1.0
 * @since 1.0
 */
public class NettyMessage {

	private Header header;// 消息头
	private byte[] body;// 消息体
	
	public NettyMessage() {
		header = new Header();
	}
	
	public NettyMessage(String id) {
		header = new Header(id);
	}
	
	public NettyMessage(MessageType type, String id) {
		header = new Header(type, id);
	}
	
	/**
	 * @return the crcCode
	 */
	public final int getCrcCode() {
		return header.getCrcCode();
	}

	/**
	 * @param crcCode
	 *            the crcCode to set
	 */
	public final void setCrcCode(int crcCode) {
		this.header.setCrcCode(crcCode);
	}
	
	/**
	 * @return the length
	 */
	public final int getLength() {
		return header.getLength();
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public final void setLength(int length) {
		header.setLength(length);
	}
	
	public String getId() {
		return header.getId();
	}
	
	public void setId(String id) {
		header.setId(id);
	}

	/**
	 * @return the sessionID
	 */
	public final long getSessionID() {
		return header.getSessionID();
	}

	/**
	 * @param sessionID
	 *            the sessionID to set
	 */
	public final void setSessionID(long sessionID) {
		header.setSessionID(sessionID);
	}

	/**
	 * @return the type
	 */
	public final MessageType getType() {
		return header.getType();
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public final void setType(MessageType type) {
		header.setType(type);
	}
	
	public int getBusinessType() {
		return header.getBusinessType();
	}

	public void setBusinessType(int businessType) {
		header.setBusinessType(businessType);
	}

	/**
	 * @return the priority
	 */
	public final byte getPriority() {
		return header.getPriority();
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public final void setPriority(byte priority) {
		header.setPriority(priority);
	}

	/**
	 * @return the attachment
	 */
	public final Map<String, Object> getAttachment() {
		return header.getAttachment();
	}

	/**
	 * @param attachment
	 *            the attachment to set
	 */
	public final void setAttachment(Map<String, Object> attachment) {
		header.setAttachment(attachment);
	}

	
	/**
	 * @return the body
	 */
	public final byte[] getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public final void setBody(byte[] body) {
		this.body = body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NettyMessage [header=" + header + "]:" + ByteUtil.bytes2HexString(this.body);
	}
}
