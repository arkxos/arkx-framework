package io.arkx.framework.avatarmq.netty;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.arkx.framework.avatarmq.core.CallBackInvoker;
import io.arkx.framework.avatarmq.core.CallBackListener;
import io.arkx.framework.avatarmq.core.NotifyCallback;
import io.arkx.framework.avatarmq.model.RequestMessage;
import io.arkx.framework.avatarmq.model.ResponseMessage;
import io.arkx.framework.avatarmq.msg.ProducerAckMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @filename:MessageProcessor.java
 * @description:MessageProcessor功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class MessageProcessor {

    private MessageConnectFactory factory;
    private MessageConnectPool pool;

    public MessageProcessor(String serverAddress) {
        MessageConnectPool.setServerAddress(serverAddress);
        pool = MessageConnectPool.getMessageConnectPoolInstance();
        this.factory = pool.borrow();
    }

    public void closeMessageConnectFactory() {
        pool.restore();
    }

    public MessageConnectFactory getMessageConnectFactory() {
        return factory;
    }

    public void sendAsynMessage(RequestMessage request, final NotifyCallback listener) {
        Channel channel = factory.getMessageChannel();
        if (channel == null) {
            return;
        }

        Map<String, CallBackInvoker<Object>> callBackMap = factory.getCallBackMap();

        CallBackInvoker<Object> invoker = new CallBackInvoker<Object>();
        callBackMap.put(request.getMsgId(), invoker);

        invoker.setRequestId(request.getMsgId());

        invoker.join(new CallBackListener<Object>() {
            public void onCallBack(Object t) {
                ResponseMessage response = (ResponseMessage) t;
                listener.onEvent((ProducerAckMessage) response.getMsgParams());

            }
        });

        ChannelFuture channelFuture = channel.writeAndFlush(request);
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    invoker.setReason(future.cause());
                }
            }
        });

    }

    public Object sendAsynMessage(RequestMessage request) {
        Channel channel = factory.getMessageChannel();

        if (channel == null) {
            return null;
        }

        Map<String, CallBackInvoker<Object>> callBackMap = factory.getCallBackMap();

        CallBackInvoker<Object> invoker = new CallBackInvoker<>();
        callBackMap.put(request.getMsgId(), invoker);
        invoker.setRequestId(request.getMsgId());

        ChannelFuture channelFuture = channel.writeAndFlush(request);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    invoker.setReason(future.cause());
                }
            }
        });

        try {
            Object result = invoker.getMessageResult(factory.getTimeOut(), TimeUnit.MILLISECONDS);
            callBackMap.remove(request.getMsgId());
            return result;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendSyncMessage(RequestMessage request) {
        Channel channel = factory.getMessageChannel();

        if (channel == null) {
            return;
        }

        Map<String, CallBackInvoker<Object>> callBackMap = factory.getCallBackMap();

        CallBackInvoker<Object> invoker = new CallBackInvoker<>();
        callBackMap.put(request.getMsgId(), invoker);

        invoker.setRequestId(request.getMsgId());

        ChannelFuture channelFuture;
        try {
            channelFuture = channel.writeAndFlush(request).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        invoker.setReason(future.cause());
                    }
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(MessageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
