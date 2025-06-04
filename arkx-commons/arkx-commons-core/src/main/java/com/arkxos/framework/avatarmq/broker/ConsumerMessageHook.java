package com.arkxos.framework.avatarmq.broker;

import com.arkxos.framework.avatarmq.consumer.ConsumerContext;
import com.arkxos.framework.avatarmq.model.RemoteChannelData;
import com.arkxos.framework.avatarmq.model.SubscriptionData;
import com.arkxos.framework.avatarmq.msg.SubscribeMessage;

/**
 * @filename:ConsumerMessageHook.java
 * @description:ConsumerMessageHook功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class ConsumerMessageHook implements ConsumerMessageListener {

    public ConsumerMessageHook() {

    }

    public void hookConsumerMessage(SubscribeMessage request, RemoteChannelData channel) {

        System.out.println("receive subcript info groupid:" + request.getClusterId() + " topic:" + request.getTopic() + " clientId:" + channel.getClientId());

        SubscriptionData subscript = new SubscriptionData();

        subscript.setTopic(request.getTopic());
        channel.setSubcript(subscript);

        ConsumerContext.addClusters(request.getClusterId(), channel);
    }
}
