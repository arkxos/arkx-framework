package com.rapidark.framework.message.tcp.extend;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rapidark.framework.message.tcp.MessageType;
import com.rapidark.framework.message.tcp.NettyBusinessType;
import com.rapidark.framework.message.tcp.struct.NettyMessage;
import com.rapidark.framework.message.tcp.struct.ResponseMessage;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 握手接入和安全认证
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:52:21
 * @version 1.0
 * @since 1.0
 */
public class LoginAuthResponseHandler extends SimpleChannelInboundHandler<NettyMessage> {

	private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();
	private String[] whitekList = { "127.0.0.1", "192.168.1.104" };

	private boolean enableWhiteList = false;
	
	public LoginAuthResponseHandler(boolean enableWhiteList) {
		this.enableWhiteList = enableWhiteList;
	}
	
	/**
	 * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward to
	 * the next {@link ChannelHandler} in the {@link ChannelPipeline}.
	 * 
	 * Sub-classes may override this method to change behavior.
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
		// 如果是握手请求消息，处理，其它消息透传
		if(message.getType() == MessageType.REQUEST && message.getBusinessType() == NettyBusinessType.LOGIN.value()) {
			String nodeIndex = ctx.channel().remoteAddress().toString();
			NettyMessage loginResp = null;
			// 重复登陆，拒绝
			if (nodeCheck.containsKey(nodeIndex)) {
				loginResp = buildResponse(message.getId(), (byte) -1);
			} else {
				InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
				String ip = address.getAddress().getHostAddress();
				boolean isOK = false;
				if(enableWhiteList) {
					for (String WIP : whitekList) {
						if (WIP.equals(ip)) {
							isOK = true;
							break;
						}
					}
				} else {
					isOK = true;
				}
				loginResp = isOK ? buildResponse(message.getId(), (byte) 0) : buildResponse(message.getId(), (byte) -1);
				if (isOK) {
					nodeCheck.put(nodeIndex, true);
				}
			}
			System.out.println("The login response is : " + loginResp + " body [" + loginResp.getBody() + "]");
			ctx.writeAndFlush(loginResp);
		} else {
			NettyMessage response = buildResponse2(message.getId());
			response.setId(message.getId());
			ctx.writeAndFlush(response);
			
			ctx.fireChannelRead(message);
		}
	}

	private NettyMessage buildResponse2(String requestId) {
		ResponseMessage message = new ResponseMessage(requestId);
		message.setBusinessType(0);
		return message;
	}
	
	private NettyMessage buildResponse(String requestId, byte result) {
		ResponseMessage message = new ResponseMessage(requestId);
		message.setBusinessType(NettyBusinessType.LOGIN.value());
		message.setBody(new byte[] {result});
		return message;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		nodeCheck.remove(ctx.channel().remoteAddress().toString());// 删除缓存
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}
}
