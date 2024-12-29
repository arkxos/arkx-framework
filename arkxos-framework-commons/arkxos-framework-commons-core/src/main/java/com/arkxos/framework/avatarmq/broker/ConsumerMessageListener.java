package com.arkxos.framework.avatarmq.broker;

import com.arkxos.framework.avatarmq.model.RemoteChannelData;
import com.arkxos.framework.avatarmq.msg.SubscribeMessage;

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
