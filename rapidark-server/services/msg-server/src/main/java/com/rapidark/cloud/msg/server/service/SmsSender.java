package com.rapidark.cloud.msg.server.service;

import com.rapidark.cloud.msg.client.model.BaseMessage;

/**
 * @author woodev
 */
public interface SmsSender {
    /**
     * 发送短信
     *
     * @param parameter
     * @return
     */
    Boolean send(BaseMessage parameter);
}
