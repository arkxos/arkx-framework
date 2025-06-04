package com.arkxos.framework.message.tcp.codec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.arkxos.framework.commons.util.UuidUtil;
import com.arkxos.framework.message.tcp.MessageType;
import com.arkxos.framework.message.tcp.struct.NettyMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Darkness
 * @date 2017年4月11日 下午4:06:38
 * @version 1.0
 * @since 1.0
 */
public class TestCodeC {

	MarshallingEncoder marshallingEncoder;
	MarshallingDecoder marshallingDecoder;

	public TestCodeC() throws IOException {
		marshallingDecoder = new MarshallingDecoder();
		marshallingEncoder = new MarshallingEncoder();
	}

	public NettyMessage getMessage() {
		NettyMessage nettyMessage = new NettyMessage(UuidUtil.base58Uuid());
		nettyMessage.setLength(123);
		nettyMessage.setSessionID(99999);
		nettyMessage.setType(MessageType.REQUEST);
		nettyMessage.setPriority((byte) 7);
		Map<String, Object> attachment = new HashMap<>();
		for (int i = 0; i < 10; i++) {
			attachment.put("ciyt --> " + i, "sky " + i);
		}
		nettyMessage.setAttachment(attachment);
//		nettyMessage.setBody("abcdefg-----------------------AAAAAA");
		return nettyMessage;
	}

	public ByteBuf encode(NettyMessage msg) throws Exception {
		ByteBuf sendBuf = Unpooled.buffer();
		sendBuf.writeInt((msg.getCrcCode()));
		sendBuf.writeInt((msg.getLength()));
		sendBuf.writeLong((msg.getSessionID()));
		sendBuf.writeByte((msg.getType().value()));
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
			marshallingEncoder.encode(msg.getBody(), sendBuf);
		} else
			sendBuf.writeInt(0);
		sendBuf.setInt(4, sendBuf.readableBytes());
		return sendBuf;
	}

	public NettyMessage decode(ByteBuf in) throws Exception {
		NettyMessage message = new NettyMessage(UuidUtil.base58Uuid());
		message.setCrcCode(in.readInt());
		message.setLength(in.readInt());
		message.setSessionID(in.readLong());
		message.setType(MessageType.valueOf(in.readByte()));
		message.setPriority(in.readByte());

		int size = in.readInt();
		if (size > 0) {
			Map<String, Object> attch = new HashMap<>(size);
			int keySize = 0;
			byte[] keyArray = null;
			String key = null;
			for (int i = 0; i < size; i++) {
				keySize = in.readInt();
				keyArray = new byte[keySize];
				in.readBytes(keyArray);
				key = new String(keyArray, "UTF-8");
				attch.put(key, marshallingDecoder.decode(in));
			}
			keyArray = null;
			key = null;
			message.setAttachment(attch);
		}
		if (in.readableBytes() > 4) {
//			message.setBody(marshallingDecoder.decode(in));
		}
		return message;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TestCodeC testC = new TestCodeC();
		NettyMessage message = testC.getMessage();
		System.out.println(message + "[body ] " + message.getBody());

		for (int i = 0; i < 5; i++) {
			ByteBuf buf = testC.encode(message);
			NettyMessage decodeMsg = testC.decode(buf);
			System.out.println(decodeMsg + "[body ] " + decodeMsg.getBody());
			System.out.println("-------------------------------------------------");
		}

	}

}
