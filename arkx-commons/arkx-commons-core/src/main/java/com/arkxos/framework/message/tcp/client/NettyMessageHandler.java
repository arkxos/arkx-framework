package com.arkxos.framework.message.tcp.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkxos.framework.message.tcp.struct.NettyMessage;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 握手认证客户端
 * @author Darkness
 * @date 2017年4月11日 下午3:47:24
 * @version 1.0
 * @since 1.0
 */
public class NettyMessageHandler extends SimpleChannelInboundHandler<NettyMessage> {

	private Logger logger = LoggerFactory.getLogger(NettyMessageHandler.class);
	
	private NettyClient client;
	
	public NettyMessageHandler(NettyClient client) {
		this.client = client;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}
	
	/**
	 * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward to
	 * the next {@link ChannelHandler} in the {@link ChannelPipeline}.
	 * 
	 * Sub-classes may override this method to change behavior.
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
		client.getMessageProcessor().onMessage(message);
		client.onMessage(message);
		ctx.fireChannelRead(message);
	}

}
