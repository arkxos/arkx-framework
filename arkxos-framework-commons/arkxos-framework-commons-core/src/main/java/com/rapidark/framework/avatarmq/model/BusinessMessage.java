package com.rapidark.framework.avatarmq.model;

import java.io.Serializable;

import com.rapidark.framework.avatarmq.msg.BaseMessage;

/**
 * @filename:BusinessMessage.java
 * @description:BusinessMessage功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public abstract class BusinessMessage implements Serializable {

    public final static int SUCCESS = 0;
    public final static int FAIL = 1;
    protected String msgId;
    protected BaseMessage msgParams;
    protected MessageSource msgSource;
    protected MessageType msgType;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public BaseMessage getMsgParams() {
        return msgParams;
    }

    public void setMsgParams(BaseMessage msgParams) {
        this.msgParams = msgParams;
    }

    public MessageSource getMsgSource() {
        return msgSource;
    }

    public void setMsgSource(MessageSource msgSource) {
        this.msgSource = msgSource;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }
}
