package com.rapidark.framework.message.tcp.extend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidark.framework.commons.util.UuidUtil;
import com.rapidark.framework.message.tcp.NettyBusinessType;
import com.rapidark.framework.message.tcp.client.NettyClient;
import com.rapidark.framework.message.tcp.struct.NettyMessage;
import com.rapidark.framework.message.tcp.struct.RequestMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 心跳请求消息处理
 * @author darkness
 *
 */
public class LoginAuthRequestHandler extends SimpleChannelInboundHandler<NettyMessage> {

	private Logger logger = LoggerFactory.getLogger(LoginAuthRequestHandler.class);
	
	private NettyClient client;
	
	public LoginAuthRequestHandler(NettyClient client) {
		this.client = client;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		authenticate();
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
		ctx.fireChannelRead(message);
	}

	private void authenticate() {
		logger.debug("prepared for authenticate");
		NettyMessage message = client.getMessageProcessor().send(buildLoginRequest());
		logger.debug("authenticate success");
		// 如果是握手应答消息，需要判断是否认证成功
		
		byte loginResult = message.getBody()[0];
		if (loginResult != (byte) 0) {
			// 握手失败，关闭连接
			client.shutdown();
		} else {
			logger.info("Login is ok : " + message);
		}
	}
	
	private NettyMessage buildLoginRequest() {
		RequestMessage message = new RequestMessage(UuidUtil.base58Uuid());
		message.setBusinessType(NettyBusinessType.LOGIN.value());
		return message;
	}

}
