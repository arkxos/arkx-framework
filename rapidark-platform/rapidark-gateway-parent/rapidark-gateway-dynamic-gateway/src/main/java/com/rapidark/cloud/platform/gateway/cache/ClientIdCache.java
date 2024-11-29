package com.rapidark.cloud.platform.gateway.cache;

import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存客户端信息
 * @author darkness
 * @date 2022/5/30 17:17
 * @version 1.0
 */
@Deprecated
public class ClientIdCache {

    private static ConcurrentHashMap<String,Object> cacheMap = new ConcurrentHashMap<>();

    public static void put(final String key,final Object value){
        Assert.notNull(key, "hash map key cannot is null");
        Assert.notNull(value, "hash map value cannot is null");
        cacheMap.put(key, value);
    }

    public static Object get(final String key){
        return cacheMap.get(key);
    }

    public static synchronized void remove(final String key){
        if (cacheMap.containsKey(key)){
            cacheMap.remove(key);
        }
    }

    public static synchronized void clear(){
        cacheMap.clear();
    }

    public static ConcurrentHashMap<String,Object> getCacheMap(){
        return cacheMap;
    }
}
