package io.arkx.framework.avatarmq.core;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * @filename:ChannelCache.java
 * @description:ChannelCache功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class ChannelCache {

    private static ConcurrentHashMap<String, Channel> producerMap = new ConcurrentHashMap<String, Channel>();

    public static void pushRequest(String requestId, Channel channel) {
        producerMap.put(requestId, channel);
    }

    public static Channel findChannel(String requestId) {
        Channel channel = producerMap.remove(requestId);
        return channel;
    }
}
