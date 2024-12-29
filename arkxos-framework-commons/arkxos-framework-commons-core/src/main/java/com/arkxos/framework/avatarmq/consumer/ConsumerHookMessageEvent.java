package com.arkxos.framework.avatarmq.consumer;

import com.arkxos.framework.avatarmq.core.HookMessageEvent;
import com.arkxos.framework.avatarmq.model.ResponseMessage;
import com.arkxos.framework.avatarmq.msg.ConsumerAckMessage;
import com.arkxos.framework.avatarmq.msg.Message;

/**
 * @filename:ConsumerHookMessageEvent.java
 * @description:ConsumerHookMessageEvent功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class ConsumerHookMessageEvent extends HookMessageEvent<Object> {

    private ProducerMessageHook hook;

    public ConsumerHookMessageEvent(ProducerMessageHook hook) {
        this.hook = hook;
    }

    public Object callBackMessage(Object obj) {
        ResponseMessage response = (ResponseMessage) obj;
        if (response.getMsgParams() instanceof Message) {
            ConsumerAckMessage result = hook.hookMessage((Message) response.getMsgParams());
            result.setMsgId(((Message) response.getMsgParams()).getMsgId());
            return result;
        } else {
            return null;
        }
    }
}
