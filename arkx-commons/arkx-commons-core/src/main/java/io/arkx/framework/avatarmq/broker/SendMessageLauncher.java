package io.arkx.framework.avatarmq.broker;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import io.arkx.framework.avatarmq.core.CallBackInvoker;
import io.arkx.framework.avatarmq.core.MessageSystemConfig;
import io.arkx.framework.avatarmq.model.ResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * @filename:SendMessageLauncher.java
 * @description:SendMessageLauncher功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class SendMessageLauncher {

    private static SendMessageLauncher resource;

    public static SendMessageLauncher getInstance() {
        if (resource == null) {
            synchronized (SendMessageLauncher.class) {
                if (resource == null) {
                    resource = new SendMessageLauncher();
                }
            }
        }
        return resource;
    }

    private long timeout = MessageSystemConfig.MessageTimeOutValue;

    public Map<String, CallBackInvoker<Object>> invokeMap = new ConcurrentSkipListMap<>();

    private SendMessageLauncher() {
    }

    public Object launcher(Channel channel, ResponseMessage response) {
        if (channel != null) {
            CallBackInvoker<Object> invoke = new CallBackInvoker<>();
            invokeMap.put(response.getMsgId(), invoke);
            invoke.setRequestId(response.getMsgId());
            ChannelFuture channelFuture = channel.writeAndFlush(response);
            channelFuture.addListener(new LauncherListener(invoke));
            try {
                Object result = invoke.getMessageResult(timeout, TimeUnit.MILLISECONDS);
                return result;
            } catch (RuntimeException e) {
                throw e;
            } finally {
                invokeMap.remove(response.getMsgId());
            }
        } else {
            return null;
        }
    }

    public boolean trace(String key) {
        return invokeMap.containsKey(key);
    }

    public CallBackInvoker<Object> detach(String key) {
        if (invokeMap.containsKey(key)) {
            return invokeMap.remove(key);
        }
        return null;
    }
}
