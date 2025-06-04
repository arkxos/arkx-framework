package io.arkx.framework.avatarmq.netty;

import io.arkx.framework.avatarmq.serialize.MessageCodecUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @filename:MessageObjectEncoder.java
 * @description:MessageObjectEncoder功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class MessageObjectEncoder extends MessageToByteEncoder<Object> {

    private MessageCodecUtil util = null;

    public MessageObjectEncoder(final MessageCodecUtil util) {
        this.util = util;
    }

    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        util.encode(out, msg);
    }
}
