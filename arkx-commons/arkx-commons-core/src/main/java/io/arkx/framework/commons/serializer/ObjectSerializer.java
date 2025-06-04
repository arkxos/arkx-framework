package io.arkx.framework.commons.serializer;

import java.lang.reflect.Type;

/**
 * 对象序列化处理器
 *  
 * @author Darkness
 * @date 2014-12-17 下午9:21:56
 * @version V1.0
 * @since ark 1.0
 */
public class ObjectSerializer extends AbstractSerializer {

    private static ObjectSerializer eventSerializer;

    /**
     * 默认的序列化处理器
     *  
     * @author Darkness
     * @date 2014-12-17 下午9:25:14
     * @version V1.0
     * @since ark 1.0
     */
    public static synchronized ObjectSerializer instance() {
        if (ObjectSerializer.eventSerializer == null) {
            ObjectSerializer.eventSerializer = new ObjectSerializer();
        }

        return ObjectSerializer.eventSerializer;
    }

    public ObjectSerializer(boolean isCompact) {
        this(false, isCompact);
    }

    public ObjectSerializer(boolean isPretty, boolean isCompact) {
        super(isPretty, isCompact);
    }

    /**
     * 反序列化对象
     *  
     * @author Darkness
     * @date 2014-12-17 下午9:30:46
     * @version V1.0
     * @since ark 1.0
     */
    public <T extends Object> T deserialize(String aSerialization, final Class<T> aType) {
        T domainEvent = this.gson().fromJson(aSerialization, aType);

        return domainEvent;
    }

    /**
     * 反序列化对象
     *  
     * @author Darkness
     * @date 2014-12-17 下午9:30:33
     * @version V1.0
     * @since ark 1.0
     */
    public <T extends Object> T deserialize(String aSerialization, final Type aType) {
        T domainEvent = this.gson().fromJson(aSerialization, aType);

        return domainEvent;
    }

    /**
     * 序列化对象
     *  
     * @author Darkness
     * @date 2014-12-17 下午9:29:30
     * @version V1.0
     * @since ark 1.0
     */
    public String serialize(Object anObject) {
        String serialization = this.gson().toJson(anObject);

        return serialization;
    }

    private ObjectSerializer() {
        this(false, false);
    }
}
