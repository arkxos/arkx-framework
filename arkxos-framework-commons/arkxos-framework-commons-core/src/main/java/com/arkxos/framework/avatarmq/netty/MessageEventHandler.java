package com.arkxos.framework.avatarmq.netty;

import io.netty.channel.ChannelHandlerContext;

/**
 * @filename:MessageEventHandler.java
 * @description:MessageEventHandler功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface MessageEventHandler {

    void handleMessage(ChannelHandlerContext ctx, Object msg);

}
