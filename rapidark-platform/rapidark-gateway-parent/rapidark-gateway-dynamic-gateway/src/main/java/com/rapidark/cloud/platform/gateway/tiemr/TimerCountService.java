package com.rapidark.cloud.platform.gateway.tiemr;

import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.framework.util.RouteConstants;
import com.rapidark.cloud.platform.gateway.framework.util.RouteUtils;
import com.rapidark.cloud.platform.gateway.cache.CountCache;
import com.rapidark.framework.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description 定时执行路由访问量统计业务类
 * @Author jianglong
 * @Date 2020/07/07
 * @Version V1.0
 */
@Slf4j
@Service
public class TimerCountService {

	private static final String FRESH = ":FRESH";

	@Resource
	private RedisUtils redisUtils;

	/**
	 * 每1分钟执行一次缓存同步
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	public void writerCache(){
		log.info("执行定时任务：统计数据同步到redis缓存...");
		//保存按分钟统计的数据
		ConcurrentHashMap<String,Integer> cacheMap = CountCache.getCacheMap();
		//深克隆map
		ConcurrentHashMap<String,Integer> minMap = new ConcurrentHashMap<>(cacheMap.size());
		minMap.putAll(cacheMap);
		//每次统计完清除缓存统计数据，重新记录下一分钟请求量(高并发下可能会存在计算误差)
		CountCache.clear();
        //拆分网关负载路由ID统计
		addRouteKey(minMap);

		//保存按分钟统计的数据,数据缓存1小时
		String freshKey = RouteConstants.COUNT_MIN_KEY + FRESH;
		String minKey = RouteConstants.COUNT_MIN_KEY + DateFormatUtils.format(new Date(), Constants.YYYYMMDDHHMM);
		String min =  DateFormatUtils.format(new Date(), Constants.YYYYMMDDHHMM);
		String minFresh = redisUtils.getString(freshKey);
		long minLong = 0;
		if(!StringUtils.isEmpty(min)) {
			minLong = Long.parseLong(min);
		}
		long minFreshLong = 0;
		if(!StringUtils.isEmpty(minFresh)) {
			minFreshLong = Long.parseLong(minFresh);
		}

		if (StringUtils.isEmpty(minFresh) || minLong > minFreshLong){
			this.recordCountCache(freshKey, min, minKey, minMap, 1, TimeUnit.HOURS);
		}else {
			this.syncCountCache(minKey, minMap);
		}

		//保存按小时统计的数据,数据缓存24小时
		freshKey = RouteConstants.COUNT_HOUR_KEY + FRESH;
		// FISH_GATEWAY_COUNT:HOUR:2022060713
		String hourKey = RouteConstants.COUNT_HOUR_KEY + DateFormatUtils.format(new Date(), Constants.YYYYMMDDHH);
		String hour =  DateFormatUtils.format(new Date(), Constants.YYYYMMDDHH);
		String hourFresh = redisUtils.getString(freshKey);
		if (StringUtils.isEmpty(hourFresh) || Long.parseLong(hour) > Long.parseLong(hourFresh)){
			this.recordCountCache(freshKey, hour, hourKey, minMap, 24, TimeUnit.HOURS);
		}else {
			this.syncCountCache(hourKey, minMap);
		}

		//保存按天统计的数据,数据缓存7天
		freshKey = RouteConstants.COUNT_DAY_KEY + FRESH;
		String dayKey = RouteConstants.COUNT_DAY_KEY + DateFormatUtils.format(new Date(), Constants.YYYYMMDD);
		String day =  DateFormatUtils.format(new Date(), Constants.YYYYMMDD);
		String dayFresh = redisUtils.getString(freshKey);
		if (StringUtils.isEmpty(dayFresh) || Long.parseLong(day) > Long.parseLong(dayFresh)){
			this.recordCountCache(freshKey, day, dayKey, minMap, 7, TimeUnit.DAYS);
		}else {
			this.syncCountCache(dayKey, minMap);
		}
	}

	/**
	 * 对负载网关路由进行访问后，也需要增加对路由ID的次数统计
	 * @param value
	 */
	private void addRouteKey(ConcurrentHashMap<String,Integer> value){
		ConcurrentHashMap<String,Integer> routeMap = new ConcurrentHashMap<>();
		value.forEach((k,v)->{
			if (k.contains(RouteConstants.BALANCED)){
				routeMap.put(RouteUtils.getBalancedToRouteId(k), v);
			}
		});
		if (!routeMap.isEmpty()){
			value.putAll(routeMap);
		}
	}

	/**
	 * 设置缓存数据
	 * @param freshKey
	 * @param freshValue
	 * @param key
	 * @param value
	 * @param timeout
	 * @param timeUnit
	 */
	private void recordCountCache(String freshKey, String freshValue, String key, ConcurrentHashMap<String,Integer> value, int timeout, TimeUnit timeUnit){
		redisUtils.set(freshKey, freshValue);
		boolean exist = redisUtils.hasKey(key);
		Map<String,String> cacheMap = new HashMap<>(value.size());
		value.forEach((k,v)->cacheMap.put(k,String.valueOf(v)));
		redisUtils.setMap(key, cacheMap);
		//新增key设置过期时间
		if (!exist){
			//redis过期清除指定KEY缓存数据
			redisUtils.expire(key, timeout, timeUnit);
		}
	}

	/**
	 * 考虑网关服务的集群式架构设计，需要累加已知统计缓存数据
	 * @param key
	 * @param map
	 */
	public void syncCountCache(String key, Map<String,Integer> map){
		map.forEach((k,v)->{
			String value = (String) redisUtils.hget(key, k);
			if (StringUtils.isNotBlank(value)){
				int count = 0;
				try{
					count = Integer.parseInt(value);
				}catch(Exception e){
					log.error("redis get key:{} & field:{} to value is null", key, k);
				}
				v += count;
			}
			redisUtils.hset(key, k, String.valueOf(v));
		});
	}

}
