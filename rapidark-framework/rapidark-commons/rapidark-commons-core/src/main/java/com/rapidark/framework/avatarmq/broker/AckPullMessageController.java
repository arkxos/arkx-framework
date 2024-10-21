package com.rapidark.framework.avatarmq.broker;

import java.util.concurrent.Callable;

import com.rapidark.framework.avatarmq.core.AckTaskQueue;
import com.rapidark.framework.avatarmq.core.ChannelCache;
import com.rapidark.framework.avatarmq.core.MessageSystemConfig;
import com.rapidark.framework.avatarmq.core.SemaphoreCache;
import com.rapidark.framework.avatarmq.model.MessageSource;
import com.rapidark.framework.avatarmq.model.MessageType;
import com.rapidark.framework.avatarmq.model.ResponseMessage;
import com.rapidark.framework.avatarmq.msg.ProducerAckMessage;
import com.rapidark.framework.avatarmq.netty.NettyUtil;

import io.netty.channel.Channel;

/**
 * @filename:AckPullMessageController.java
 * @description:AckPullMessageController功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AckPullMessageController implements Callable<Void> {

    private volatile boolean stoped = false;

    public void stop() {
        stoped = true;
    }

    public boolean isStoped() {
        return stoped;
    }

    public Void call() {
        while (!stoped) {
            SemaphoreCache.acquire(MessageSystemConfig.AckTaskSemaphoreValue);
            ProducerAckMessage ack = AckTaskQueue.getAck();
            String requestId = ack.getAck();
            ack.setAck("");

            Channel channel = ChannelCache.findChannel(requestId);
            if (NettyUtil.validateChannel(channel)) {
                ResponseMessage response = new ResponseMessage();
                response.setMsgId(requestId);
                response.setMsgSource(MessageSource.AvatarMQBroker);
                response.setMsgType(MessageType.AvatarMQProducerAck);
                response.setMsgParams(ack);

                channel.writeAndFlush(response);
            }
        }
        return null;
    }
}
