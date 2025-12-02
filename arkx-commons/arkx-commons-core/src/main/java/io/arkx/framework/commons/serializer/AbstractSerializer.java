package io.arkx.framework.commons.serializer;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;

/**
 * 抽象序列化处理器
 *
 * @author Darkness
 * @date 2014-5-5 下午8:09:47
 * @version V1.0
 */
public class AbstractSerializer {

    private static Map<Class<?>, Object> typeAdapters = new HashMap<Class<?>, Object>();

    public static void registerTypeAdapter(Class<?> classType, Object serializer) {
        typeAdapters.put(classType, serializer);
    }

    private Gson gson;

    protected AbstractSerializer(boolean isCompact) {
        this(false, isCompact);
    }

    protected AbstractSerializer(boolean isPretty, boolean isCompact) {
        super();

        if (isPretty && isCompact) {
            this.buildForPrettyCompact();
        } else if (isCompact) {
            this.buildForCompact();
        } else {
            this.build();
        }
    }

    protected Gson gson() {
        return this.gson;
    }

    private void build() {
        GsonBuilder builder = new GsonBuilder();
        for (Class<?> type : typeAdapters.keySet()) {
            builder.registerTypeAdapter(type, typeAdapters.get(type));
        }

        this.gson = builder.registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapter(Date.class, new DateDeserializer()).serializeNulls().create();
    }

    private void buildForCompact() {
        GsonBuilder builder = new GsonBuilder();
        for (Class<?> type : typeAdapters.keySet()) {
            builder.registerTypeAdapter(type, typeAdapters.get(type));
        }

        this.gson = builder.registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapter(Date.class, new DateDeserializer()).create();
    }

    private void buildForPrettyCompact() {
        GsonBuilder builder = new GsonBuilder();
        for (Class<?> type : typeAdapters.keySet()) {
            builder.registerTypeAdapter(type, typeAdapters.get(type));
        }

        this.gson = builder.registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapter(Date.class, new DateDeserializer()).setPrettyPrinting().create();
    }

    private class DateSerializer implements JsonSerializer<Date> {
        public JsonElement serialize(Date source, Type typeOfSource, JsonSerializationContext context) {
            return new JsonPrimitive(Long.toString(source.getTime()));
        }
    }

    private class DateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfTarget, JsonDeserializationContext context)
                throws JsonParseException {
            long time = Long.parseLong(json.getAsJsonPrimitive().getAsString());
            return new Date(time);
        }
    }
}
