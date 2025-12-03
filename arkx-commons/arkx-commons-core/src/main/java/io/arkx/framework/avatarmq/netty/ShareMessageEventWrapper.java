package io.arkx.framework.avatarmq.netty;

import io.arkx.framework.avatarmq.core.HookMessageEvent;
import io.netty.channel.ChannelHandler;

/**
 * @filename:ShareMessageEventWrapper.java
 * @description:ShareMessageEventWrapper功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
@ChannelHandler.Sharable
public class ShareMessageEventWrapper<T> extends MessageEventWrapper<T> {

    public ShareMessageEventWrapper() {
        super.setWrapper(this);
    }

    public ShareMessageEventWrapper(MessageProcessor processor) {
        super(processor, null);
        super.setWrapper(this);
    }

    public ShareMessageEventWrapper(MessageProcessor processor, HookMessageEvent<T> hook) {
        super(processor, hook);
        super.setWrapper(this);
    }

}
