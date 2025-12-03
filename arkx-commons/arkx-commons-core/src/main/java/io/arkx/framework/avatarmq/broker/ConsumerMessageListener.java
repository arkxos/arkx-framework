package io.arkx.framework.avatarmq.broker;

import io.arkx.framework.avatarmq.model.RemoteChannelData;
import io.arkx.framework.avatarmq.msg.SubscribeMessage;

/**
 * @filename:ConsumerMessageListener.java
 * @description:ConsumerMessageListener功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface ConsumerMessageListener {

    void hookConsumerMessage(SubscribeMessage msg, RemoteChannelData channel);

}
