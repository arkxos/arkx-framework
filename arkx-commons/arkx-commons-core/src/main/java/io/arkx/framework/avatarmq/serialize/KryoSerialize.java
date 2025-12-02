//package io.arkx.framework.avatarmq.serialize;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import com.esotericsoftware.kryo.Kryo;
//import com.esotericsoftware.kryo.io.Input;
//import com.esotericsoftware.kryo.io.Output;
//import com.esotericsoftware.kryo.pool.KryoPool;
//import com.google.commons.io.Closer;
//
///**
// * @filename:KryoSerialize.java
// * @description:KryoSerialize功能模块
// * @author tangjie<https://github.com/tang-jie>
// * @blog http://www.cnblogs.com/jietang/
// * @since 2016-8-11
// */
//public class KryoSerialize {
//
//    private KryoPool pool = null;
//    private Closer closer = Closer.create();
//
//    public KryoSerialize(final KryoPool pool) {
//        this.pool = pool;
//    }
//
//    public void serialize(OutputStream output, Object object) throws IOException {
//        try {
//            Kryo kryo = pool.borrow();
//            Output out = new Output(output);
//            closer.register(out);
//            closer.register(output);
//            kryo.writeClassAndObject(out, object);
//            pool.release(kryo);
//        } finally {
//            closer.close();
//        }
//    }
//
//    public Object deserialize(InputStream input) throws IOException {
//        try {
//            Kryo kryo = pool.borrow();
//            Input in = new Input(input);
//            closer.register(in);
//            closer.register(input);
//            Object result = kryo.readClassAndObject(in);
//            pool.release(kryo);
//            return result;
//        } finally {
//            closer.close();
//        }
//    }
//}
package io.arkx.framework.avatarmq.serialize;
