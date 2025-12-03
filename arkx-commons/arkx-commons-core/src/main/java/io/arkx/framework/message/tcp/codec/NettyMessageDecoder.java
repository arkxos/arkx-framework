package io.arkx.framework.message.tcp.codec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.arkx.framework.message.tcp.MessageType;
import io.arkx.framework.message.tcp.struct.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 消息解码
 *
 * @author Darkness
 * @date 2017年4月11日 下午3:45:08
 * @version 1.0
 * @since 1.0
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        NettyMessage message = new NettyMessage();
        message.setCrcCode(frame.readInt());
        message.setLength(frame.readInt());

        byte[] uuidBytes = new byte[22];
        frame.readBytes(uuidBytes);
        // String encodedUuid = new String(encodedUuidBytes);
        // byte[] uuidBytes = Base58.decode(encodedUuid);
        String uuid = new String(uuidBytes);

        message.setId(uuid);

        message.setSessionID(frame.readLong());
        message.setType(MessageType.valueOf(frame.readByte()));
        message.setBusinessType(frame.readInt());
        message.setPriority(frame.readByte());

        int size = frame.readInt();
        if (size > 0) {
            Map<String, Object> attch = new HashMap<>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i = 0; i < size; i++) {
                keySize = frame.readInt();
                keyArray = new byte[keySize];
                frame.readBytes(keyArray);
                key = new String(keyArray, "UTF-8");
                attch.put(key, marshallingDecoder.decode(frame));
            }
            keyArray = null;
            key = null;
            message.setAttachment(attch);
        }
        int readable = frame.readableBytes();
        // if (frame.readableBytes() > 4) {
        // message.setBody(marshallingDecoder.decode(frame));
        // }
        byte[] bytes = new byte[readable];
        frame.readBytes(bytes);
        message.setBody(bytes);
        return message;
    }

}
