package io.arkx.framework.avatarmq.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

//import com.esotericsoftware.kryo.pool.KryoPool;

import io.netty.buffer.ByteBuf;

/**
 * @filename:KryoCodecUtil.java
 * @description:KryoCodecUtil功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class KryoCodecUtil implements MessageCodecUtil {

//    private KryoPool pool;

//    public KryoCodecUtil(KryoPool pool) {
//        this.pool = pool;
//    }

    public void encode(final ByteBuf out, final Object message) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
//            KryoSerialize kryoSerialization = new KryoSerialize(pool);
//            kryoSerialization.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        } finally {
            byteArrayOutputStream.close();
        }
    }

    public Object decode(byte[] body) throws IOException {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(body);
//            KryoSerialize kryoSerialization = new KryoSerialize(pool);
//            Object obj = kryoSerialization.deserialize(byteArrayInputStream);
//            return obj;
			return null;
        } finally {
            byteArrayInputStream.close();
        }
    }
}
