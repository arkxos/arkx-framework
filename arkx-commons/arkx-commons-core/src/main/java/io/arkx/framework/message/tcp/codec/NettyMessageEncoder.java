package io.arkx.framework.message.tcp.codec;

import java.io.IOException;
import java.util.Map;

import io.arkx.framework.message.tcp.struct.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 消息编码
 *
 * @author Darkness
 * @date 2017年4月11日 下午3:42:08
 * @version 1.0
 * @since 1.0
 */
public final class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {

    MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() throws IOException {
        this.marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf sendBuf) throws Exception {
        if (msg == null)
            throw new Exception("The encode message is null");
        sendBuf.writeInt((msg.getCrcCode()));
        sendBuf.writeInt((msg.getLength()));

        String uuid = msg.getId();
        byte[] uuidBytes = uuid.getBytes();
        // String encodedUuid = Base58.encode(uuidBytes);
        // byte[] encodedUuidBytes = encodedUuid.getBytes();

        sendBuf.writeBytes(uuidBytes);
        sendBuf.writeLong((msg.getSessionID()));
        sendBuf.writeByte((msg.getType().value()));
        sendBuf.writeInt((msg.getBusinessType()));
        sendBuf.writeByte((msg.getPriority()));
        sendBuf.writeInt((msg.getAttachment().size()));
        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> param : msg.getAttachment().entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
            marshallingEncoder.encode(value, sendBuf);
        }
        key = null;
        keyArray = null;
        value = null;
        if (msg.getBody() != null) {
            // marshallingEncoder.encode(msg.getBody(), sendBuf);
            sendBuf.writeBytes(msg.getBody());
        } else {
            // sendBuf.writeInt(0);
        }
        sendBuf.setInt(4, sendBuf.readableBytes() - 8);
    }
}
