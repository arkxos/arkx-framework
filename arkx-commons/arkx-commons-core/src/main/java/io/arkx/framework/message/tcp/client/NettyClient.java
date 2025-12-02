package io.arkx.framework.message.tcp.client;

import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.arkx.framework.commons.util.SystemInfo;
import io.arkx.framework.message.tcp.MessageProcessor;
import io.arkx.framework.message.tcp.TcpNode;
import io.arkx.framework.message.tcp.codec.NettyMessageDecoder;
import io.arkx.framework.message.tcp.codec.NettyMessageEncoder;
import io.arkx.framework.message.tcp.extend.HeartBeatRequestHandler;
import io.arkx.framework.message.tcp.extend.LoginAuthRequestHandler;
import io.arkx.framework.message.tcp.struct.NettyMessage;
import io.arkx.framework.message.tcp.util.PortScan;
import io.arkx.framework.message.tcp.util.TcpUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 客户端
 *
 * String serverAddress = "127.0.0.1:8080"; String localAddress = "127.0.0.1:12088";
 * NettyClient client = new NettyClient(serverAddress, localAddress); client.start();
 *
 * @author Darkness
 * @date 2017年4月11日 下午3:58:45
 * @version 1.0
 * @since 1.0
 */
public class NettyClient implements TcpNode {

	private Logger logger = LoggerFactory.getLogger(NettyClient.class);

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	private EventLoopGroup group = new NioEventLoopGroup();

	private SocketAddress serverAddress;

	private SocketAddress localAddress;

	private MessageProcessor messageProcessor;

	// private List<ClientMessageHandler> messageHandlers = new ArrayList<>();

	public NettyClient(String serverAddress) {
		this(serverAddress, SystemInfo.ip() + ":" + PortScan.findUnusedPort());
	}

	public NettyClient(String serverAddress, String localAddress) {
		this.serverAddress = TcpUtil.string2SocketAddress(serverAddress);
		this.localAddress = TcpUtil.string2SocketAddress(localAddress);
	}

	private Channel channel;

	@Override
	public void start() throws Exception {
		logger.debug("prepared for start");
		// 连接服务器
		this.connect();
		// 连接失败，请检查服务器ip、端口号是否正确
		logger.debug("start success");
	}

	@Override
	public void shutdown() {
	}

	// protected void registerDefaultComp() {
	// registerMessageHandler(new LoginAuthClientComp(this));
	// registerMessageHandler(new HeartBeatClientComp(this));
	// }

	private void connect() throws Exception {
		logger.debug("prepared for connection");
		// 配置客户端NIO线程组
		try {

			// registerDefaultComp();

			Bootstrap b = new Bootstrap();
			final NettyClient client = this;
			b.group(group)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
						ch.pipeline().addLast("MessageEncoder", new NettyMessageEncoder());
						ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
						ch.pipeline().addLast("MessageHandler", new NettyMessageHandler(client));
						ch.pipeline().addLast("HeartBeatHandler", new HeartBeatRequestHandler(client));
						ch.pipeline().addLast("LoginAuthHandler", new LoginAuthRequestHandler(client));

						// ch.pipeline().addLast(new NettyMessageHandler());
					}
				});
			// 发起异步连接操作
			ChannelFuture future = b.connect(serverAddress, localAddress).sync();

			channel = future.channel();

			messageProcessor = new MessageProcessor(this, channel);

			logger.info("connection success");

			reconnectOnClose(future);
		}
		catch (Exception e) {
			this.connect();
		}
		finally {
		}
	}

	private void reconnectOnClose(final ChannelFuture future) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					future.channel().closeFuture().sync();
				}
				catch (InterruptedException e) {
					// e.printStackTrace();
				}
				finally {
					// 所有资源释放完成之后，清空资源，再次发起重连操作
					executor.execute(new Runnable() {
						@Override
						public void run() {
							try {
								TimeUnit.SECONDS.sleep(1);
								connect();// 发起重连操作
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		}).start();
	}

	public MessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public NettyMessage sendMessage(NettyMessage message) {
		return messageProcessor.send(message);
	}

	// public void registerMessageHandler(ClientMessageHandler handler) {
	// messageHandlers.add(handler);
	// }

	// public void onChannelActive(ChannelHandlerContext ctx) {
	// for (ClientMessageHandler messageHandler : messageHandlers) {
	// messageHandler.channelActive(ctx);
	// }
	// }

	public void onMessage(NettyMessage message) {
		// getMessageProcessor().onMessage(message);
	}

	// public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	// for (ClientMessageHandler messageHandler : messageHandlers) {
	// messageHandler.exceptionCaught(ctx, cause);
	// }
	// }

	// public List<ClientMessageHandler> getMessageHandlers() {
	// return messageHandlers;
	// }

}
