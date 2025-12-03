package io.arkx.framework.message.tcp.extend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.arkx.framework.message.tcp.MessageType;
import io.arkx.framework.message.tcp.NettyBusinessType;
import io.arkx.framework.message.tcp.struct.NettyMessage;
import io.arkx.framework.message.tcp.struct.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 心跳应答消息处理
 *
 * @author Darkness
 * @date 2017年4月11日 下午3:57:07
 * @version 1.0
 * @since 1.0
 */
public class HeartBeatResponseHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private Logger logger = LoggerFactory.getLogger(HeartBeatResponseHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
        // 返回心跳应答消息
        if (message.getType() == MessageType.REQUEST
                && message.getBusinessType() == NettyBusinessType.HEARTBEAT.value()) {
            logger.debug("Receive client heart beat message : ---> " + message);
            NettyMessage heartBeat = buildHeatBeat(message.getId());
            logger.debug("Send heart beat response message to client : ---> " + heartBeat);
            ctx.writeAndFlush(heartBeat);
        } else {
            ctx.fireChannelRead(message);
        }
    }

    private NettyMessage buildHeatBeat(String id) {
        ResponseMessage message = new ResponseMessage(id);
        message.setBusinessType(NettyBusinessType.HEARTBEAT.value());
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
    }

}
