package io.arkx.framework.avatarmq.broker;

import io.arkx.framework.avatarmq.msg.Message;

import io.netty.channel.Channel;

/**
 * @filename:ProducerMessageListener.java
 * @description:ProducerMessageListener功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface ProducerMessageListener {

    void hookProducerMessage(Message msg, String requestId, Channel channel);
}
