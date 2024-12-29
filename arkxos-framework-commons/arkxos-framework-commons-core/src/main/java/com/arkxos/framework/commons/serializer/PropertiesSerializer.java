package com.arkxos.framework.commons.serializer;

import java.util.Properties;

import com.google.gson.JsonObject;

/**
 * 属性序列化处理器
 *  
 * @author Darkness
 * @date 2014-12-17 下午9:32:12
 * @version V1.0
 * @since ark 1.0
 */
public class PropertiesSerializer extends AbstractSerializer {

    private static PropertiesSerializer propertiesSerializer;

    /**
     * 默认的序列化处理器
     *  
     * @author Darkness
     * @date 2014-12-17 下午9:31:11
     * @version V1.0
     * @since ark 1.0
     */
    public static synchronized PropertiesSerializer instance() {
        if (PropertiesSerializer.propertiesSerializer == null) {
            PropertiesSerializer.propertiesSerializer = new PropertiesSerializer(false);
        }

        return PropertiesSerializer.propertiesSerializer;
    }

    public PropertiesSerializer(boolean isCompact) {
        this(false, isCompact);
    }

    public PropertiesSerializer(boolean isPretty, boolean isCompact) {
        super(isPretty, isCompact);
    }

    /**
     * 序列化属性
     *  
     * @author Darkness
     * @date 2014-12-17 下午9:31:58
     * @version V1.0
     * @since ark 1.0
     */
    public String serialize(Properties aProperties) {
        JsonObject object = new JsonObject();

        for (Object keyObj : aProperties.keySet()) {
            String key = keyObj.toString();
            String value = aProperties.getProperty(key);
            object.addProperty(key, value);
        }

        return object.getAsString();
    }
}
