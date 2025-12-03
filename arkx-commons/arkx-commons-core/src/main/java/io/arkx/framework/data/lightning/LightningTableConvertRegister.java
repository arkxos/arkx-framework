package io.arkx.framework.data.lightning;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Darkness
 * @date 2015年12月19日 下午7:16:04
 * @version V1.0
 * @since infinity 1.0
 */
public class LightningTableConvertRegister {

    private static Map<Class<? extends ILightningTable>, ILightningTableConvertor<? extends ILightningTable>> map = new HashMap<>();

    static {
        map.put(LightningDataTable.class, new LightningDataTableConvertor());
    }

    @SuppressWarnings("unchecked")
    public static <T extends ILightningTable> ILightningTableConvertor<T> get(Class<T> lightningTableType) {
        ILightningTableConvertor<? extends ILightningTable> convertor = map.get(lightningTableType);
        if (convertor != null) {
            return (ILightningTableConvertor<T>) convertor;
        }

        for (Class<? extends ILightningTable> key : map.keySet()) {
            if (key.isAssignableFrom(lightningTableType)) {
                return (ILightningTableConvertor<T>) map.get(key);
            }
        }

        return null;
    }

}
