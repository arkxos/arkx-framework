package io.arkx.framework.message.tcp.extend;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.arkx.framework.commons.util.UuidUtil;
import io.arkx.framework.message.tcp.MessageType;
import io.arkx.framework.message.tcp.NettyBusinessType;
import io.arkx.framework.message.tcp.client.NettyClient;
import io.arkx.framework.message.tcp.struct.NettyMessage;
import io.arkx.framework.message.tcp.struct.RequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 心跳请求消息处理
 *
 * @author Darkness
 * @date 2017年4月11日 下午3:55:10
 * @version 1.0
 * @since 1.0
 */
public class HeartBeatRequestHandler extends SimpleChannelInboundHandler<NettyMessage> {

	private Logger logger = LoggerFactory.getLogger(LoginAuthRequestHandler.class);

	private static int Rate = 5000;

	private NettyClient client;

	private volatile ScheduledFuture<?> heartBeat;

	public HeartBeatRequestHandler(NettyClient client) {
		this.client = client;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 主动发送心跳消息
		heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
		if (message.getType() == MessageType.RESPONSE
				&& message.getBusinessType() == NettyBusinessType.HEARTBEAT.value()) {
			logger.debug("Client receive server heart beat message : ---> " + message);
		}
		ctx.fireChannelRead(message);
	}

	private class HeartBeatTask implements Runnable {

		public HeartBeatTask(final ChannelHandlerContext ctx) {
		}

		@Override
		public void run() {
			long currentTime = System.nanoTime();
			long last = client.getMessageProcessor().getLastRecivedServerMessageTime();
			long timeMillis = (currentTime - last) / 1000 / 1000;
			if (timeMillis > Rate) {
				NettyMessage heatBeat = buildHeatBeat();
				logger.debug("Client send heart beat messsage to server : ---> " + heatBeat);
				client.getMessageProcessor().send(heatBeat);
			}
		}

		private NettyMessage buildHeatBeat() {
			RequestMessage message = new RequestMessage(UuidUtil.base58Uuid());
			message.setBusinessType(NettyBusinessType.HEARTBEAT.value());
			return message;
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		if (heartBeat != null) {
			heartBeat.cancel(true);
			heartBeat = null;
		}
		ctx.fireExceptionCaught(cause);
	}

}
