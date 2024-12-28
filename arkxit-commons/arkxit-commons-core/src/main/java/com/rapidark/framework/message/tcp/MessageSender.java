package com.rapidark.framework.message.tcp;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.rapidark.framework.message.tcp.struct.NettyMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class MessageSender {

	private final CountDownLatch countDownLatch = new CountDownLatch(1);
	private int TimeOut = 10_000;
	private NettyMessage messageResult;
	private Throwable reason;
	Channel channel;

	public MessageSender(Channel channel) {
		this.channel = channel;
	}

	NettyMessage send(NettyMessage message) {
		ChannelFuture channelFuture = channel.writeAndFlush(message);
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					setReason(future.cause());
				}
			}
		});

		return getMessageResult(TimeOut, TimeUnit.MILLISECONDS);
	}

	void setReason(Throwable reason) {
		this.reason = reason;
		countDownLatch.countDown();
	}

	void setMessageResult(NettyMessage messageResult) {
		this.messageResult = messageResult;
		countDownLatch.countDown();
	}

	private NettyMessage getMessageResult(long timeout, TimeUnit unit) {
		try {
			countDownLatch.await(timeout, unit);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
		if (reason != null) {
			return null;
		}
		return messageResult;
	}

}
