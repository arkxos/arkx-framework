package com.arkxos.framework.avatarmq.consumer;

import com.arkxos.framework.avatarmq.msg.ConsumerAckMessage;
import com.arkxos.framework.avatarmq.msg.Message;

/**
 * @filename:ProducerMessageHook.java
 * @description:ProducerMessageHook功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface ProducerMessageHook {

    ConsumerAckMessage hookMessage(Message paramMessage);
}
