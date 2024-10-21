package com.rapidark.framework.avatarmq.msg;

import java.io.Serializable;

/**
 * @filename:ProducerAckMessage.java
 * @description:ProducerAckMessage功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class ProducerAckMessage extends BaseMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String ack;
    private int status;
    private String msgId;

    public String getAck() {
        return ack;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

	public boolean isSuccess() {
		return getStatus() == SUCCESS;
	}
}
