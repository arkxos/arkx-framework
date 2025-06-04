package com.arkxos.framework.avatarmq.broker.strategy;

import com.arkxos.framework.avatarmq.broker.ConsumerMessageListener;
import com.arkxos.framework.avatarmq.broker.ProducerMessageListener;
import com.arkxos.framework.avatarmq.broker.SendMessageLauncher;
import com.arkxos.framework.avatarmq.core.CallBackInvoker;
import com.arkxos.framework.avatarmq.model.RequestMessage;
import com.arkxos.framework.avatarmq.model.ResponseMessage;

import io.netty.channel.ChannelHandlerContext;

/**
 * @filename:BrokerConsumerMessageStrategy.java
 * @description:BrokerConsumerMessageStrategy功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class BrokerConsumerMessageStrategy implements BrokerStrategy {

    public BrokerConsumerMessageStrategy() {
    }

    @Override
    public void messageDispatch(RequestMessage request, ResponseMessage response) {
        String key = response.getMsgId();
        if (SendMessageLauncher.getInstance().trace(key)) {
            CallBackInvoker<Object> future = SendMessageLauncher.getInstance().detach(key);
            if (future == null) {
                return;
            } else {
                future.setMessageResult(request);
            }
        } else {
            return;
        }
    }

    @Override
    public void setHookProducer(ProducerMessageListener hookProducer) {
    }

    @Override
    public void setHookConsumer(ConsumerMessageListener hookConsumer) {
    }

    @Override
    public void setChannelHandler(ChannelHandlerContext channelHandler) {
    }
}
