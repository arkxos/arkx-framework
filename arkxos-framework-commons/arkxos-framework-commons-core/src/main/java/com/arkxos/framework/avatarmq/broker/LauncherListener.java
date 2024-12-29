package com.arkxos.framework.avatarmq.broker;

import com.arkxos.framework.avatarmq.core.CallBackInvoker;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @filename:LauncherListener.java
 * @description:LauncherListener功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class LauncherListener implements ChannelFutureListener {

    private CallBackInvoker<Object> invoke = null;

    public LauncherListener(CallBackInvoker<Object> invoke) {
        this.invoke = invoke;
    }

    public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
            invoke.setReason(future.cause());
        }
    }
}
