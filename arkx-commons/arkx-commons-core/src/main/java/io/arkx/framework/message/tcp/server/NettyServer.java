package io.arkx.framework.message.tcp.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.arkx.framework.message.tcp.TcpNode;
import io.arkx.framework.message.tcp.codec.NettyMessageDecoder;
import io.arkx.framework.message.tcp.codec.NettyMessageEncoder;
import io.arkx.framework.message.tcp.extend.HeartBeatResponseHandler;
import io.arkx.framework.message.tcp.extend.LoginAuthResponseHandler;
import io.arkx.framework.message.tcp.util.TcpUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 服务器
 *
 * String serverAddress = "127.0.0.1:8080"; new
 * NettyServer(serverAddress).start();
 *
 * @author Darkness
 * @date 2017年4月11日 下午4:02:12
 * @version 1.0
 * @since 1.0
 */
public class NettyServer implements TcpNode {

    private Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private SocketAddress address;

    private boolean enableWhiteList;
    private List<ChannelHandler> channelHandlers;

    public NettyServer(String address) {
        this(address, false);
    }

    public NettyServer(String address, boolean enableWhiteList) {
        this.address = TcpUtil.string2SocketAddress(address);
        this.enableWhiteList = enableWhiteList;

        this.channelHandlers = new ArrayList<>();
    }

    public NettyServer registerChannelHandler(ChannelHandler handler) {
        this.channelHandlers.add(handler);
        return this;
    }

    @Override
    public void start() throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws IOException {
                        ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                        ch.pipeline().addLast(new NettyMessageEncoder());
                        ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                        ch.pipeline().addLast(new LoginAuthResponseHandler(enableWhiteList));
                        ch.pipeline().addLast("HeartBeatHandler", new HeartBeatResponseHandler());

                        for (ChannelHandler channelHandler : channelHandlers) {
                            ch.pipeline().addLast(channelHandler);
                        }
                    }
                });

        // 绑定端口，同步等待成功
        b.bind(address).sync();

        logger.info("Netty server start ok : " + address);
    }

    @Override
    public void shutdown() {
    }

}
