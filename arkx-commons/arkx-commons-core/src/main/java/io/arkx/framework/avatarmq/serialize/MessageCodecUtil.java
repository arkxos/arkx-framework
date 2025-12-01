package io.arkx.framework.avatarmq.serialize;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @filename:MessageCodecUtil.java
 * @description:MessageCodecUtil功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface MessageCodecUtil {

	final public static int MESSAGE_LENGTH = 4;

	void encode(final ByteBuf out, final Object message) throws IOException;

	Object decode(byte[] body) throws IOException;
}
