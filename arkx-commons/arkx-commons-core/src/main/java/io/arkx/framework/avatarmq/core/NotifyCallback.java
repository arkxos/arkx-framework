package io.arkx.framework.avatarmq.core;

import io.arkx.framework.avatarmq.msg.ProducerAckMessage;

/**
 * @filename:NotifyCallback.java
 * @description:NotifyCallback功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface NotifyCallback {

    void onEvent(ProducerAckMessage result);
}
