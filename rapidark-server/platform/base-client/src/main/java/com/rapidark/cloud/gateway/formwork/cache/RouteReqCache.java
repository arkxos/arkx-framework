package com.rapidark.cloud.gateway.formwork.cache;

import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存服务路由最后请求时间
 * @author darkness
 * @date 2022/5/30 17:22
 * @version 1.0
 */
public class RouteReqCache {

    private static ConcurrentHashMap<String,Long> cacheMap = new ConcurrentHashMap<>();

    public static void put(final String key,final Long value){
        Assert.notNull(key, "hash map key cannot is null");
        Assert.notNull(value, "hash map value cannot is null");
        cacheMap.put(key, value);
    }

    public static Long get(final String key){
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

    public static ConcurrentHashMap<String,Long> getCacheMap(){
        return cacheMap;
    }
}
