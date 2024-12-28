package com.rapidark.framework.avatarmq.msg;

import java.io.Serializable;

/**
 * @filename:UnSubscribeMessage.java
 * @description:UnSubscribeMessage功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class UnSubscribeMessage extends BaseMessage implements Serializable {

    private String consumerId;

    public UnSubscribeMessage(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
}
