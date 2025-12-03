package io.arkx.framework.avatarmq.consumer;

import io.arkx.framework.avatarmq.msg.ConsumerAckMessage;
import io.arkx.framework.avatarmq.msg.Message;

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
