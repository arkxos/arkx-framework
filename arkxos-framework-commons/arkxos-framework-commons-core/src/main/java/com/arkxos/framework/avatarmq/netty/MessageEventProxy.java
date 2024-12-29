package com.arkxos.framework.avatarmq.netty;

/**
 * @filename:MessageEventProxy.java
 * @description:MessageEventProxy功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface MessageEventProxy {

    void beforeMessage(Object msg);

    void afterMessage(Object msg);
}
