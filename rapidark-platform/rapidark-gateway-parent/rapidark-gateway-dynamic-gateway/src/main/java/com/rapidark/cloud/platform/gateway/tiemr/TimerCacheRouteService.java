package com.rapidark.cloud.platform.gateway.tiemr;

import com.rapidark.cloud.platform.gateway.framework.entity.Route;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.framework.util.RouteConstants;
import com.rapidark.cloud.platform.gateway.framework.util.RouteUtils;
import com.rapidark.cloud.platform.gateway.cache.RouteCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description 定时刷新redis中的缓存路由请求key
 * @Author JL
 * @Date 2023/10/24
 * @Version V1.0
 */
@Slf4j
@Service
public class TimerCacheRouteService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 每5分钟执行一次缓存同步（数据刷新存在一定的延迟性）
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void freshCache(){
        log.debug("执行定时任务：刷新网关路由redis缓存的请求响应列表...");
        ConcurrentHashMap<String,Object> cacheMap = RouteCache.getCacheMap();
        if (cacheMap.size() <= 0){
            return ;
        }
        // 上锁，防止分布式集群下，多个定时任务同时触发，重复执行(基于redis做遍历判断，数据量大一点，锁3分钟时间也足够了)
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(RouteConstants.CACHE_LOCK_KEY, Constants.SUCCESS, 3, TimeUnit.MINUTES);
        if (!Boolean.TRUE.equals(lock)) {
            log.info("网关路由响应缓存列表刷新中...");
            return;
        }

        ConcurrentHashMap<String,Object> dataMap = new ConcurrentHashMap<>(cacheMap.size());
        //数据对象深复制，注意：浅复制只是复制值地址，指向的存储是相同的;
        //防止数据同步过程中，写入到cacheMap，导致数据读取出错与性能影响
        dataMap.putAll(cacheMap);
        for (Map.Entry<String, Object> entry : dataMap.entrySet()){
            Route route = (Route) entry.getValue();
            String routeId = RouteUtils.getBalancedToRouteId(route.getId());

            //是否已存在缓存列表
            String key = String.format(RouteConstants.CACHE_ROUTE_KEY, routeId);
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                continue;
            }

            //获取网关路由对象，防止为负载route
            if (!routeId.equals(route.getId())) {
                route = (Route) cacheMap.get(routeId);
            }

            //如果ttl少于0，则说明已取消掉缓存
            if (route.getCacheTtl() == null || route.getCacheTtl() <= 0) {
                redisTemplate.delete(key);
                continue;
            }

            //遍历缓存请求key，如果该key已不存在，则移除指定列表中的缓存请求key名
            Set<String> sets = redisTemplate.opsForSet().members(key);
            if (CollectionUtils.isEmpty(sets)) {
                continue;
            }
            long count = sets.stream().filter(k -> !Boolean.TRUE.equals(redisTemplate.hasKey(k))).map(k -> {
                log.debug("redis get key:{} & value:{} to is expire, delete timeout cache key ... ", key, k);
                return redisTemplate.opsForSet().remove(key, k);
            }).count();
            log.info("redis get key:{} , delete cache value size:{}", key, count);
        }
        //释放锁
        redisTemplate.delete(RouteConstants.CACHE_LOCK_KEY);
    }

}
