package io.arkx.framework.boot.starter.aspect;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.arkx.framework.commons.util.EncryptUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import io.arkx.framework.commons.exception.OpenException;
import com.google.common.collect.Maps;

/**
 * @author darkness
 * @date 2021/7/12 14:29
 * @version 1.0
 */
@Aspect
@Component
public class NoRepeatSubmitAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(NoRepeatSubmitAspect.class);

    private static final String SUFFIX = "SUFFIX";

//    @Autowired
//    LockService lockService;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public NoRepeatSubmitAspect() {
        System.out.println("active NoRepeatSubmit Point");
    }

    /**
     * 横切点
     */
    @Pointcut("@annotation(noRepeatSubmit)")
    public void repeatPoint(NoRepeatSubmit noRepeatSubmit) {

    }

    /**
     *  接收请求，并记录数据
     */
    @Around(value = "repeatPoint(noRepeatSubmit)")
    public Object doBefore(ProceedingJoinPoint joinPoint, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        String key = RedisKey.NO_REPEAT_LOCK_PREFIX + noRepeatSubmit.location();
        String valueKey = RedisKey.NO_REPEAT_LOCK_VALUE_PREFIX + noRepeatSubmit.location();
        Object[] args = joinPoint.getArgs();
        String name = noRepeatSubmit.name();
        int argIndex = noRepeatSubmit.argIndex();
        String suffix;
        if (StringUtils.hasLength(name)) {
            Map<String, Object> keyAndValue = getKeyAndValue(args[argIndex]);
            Object valueObj = keyAndValue.get(name);
            if (valueObj == null) {
                suffix = SUFFIX;
            } else {
                suffix = String.valueOf(valueObj);
            }
        } else {
            String jsonString = JSON.toJSONString(args[argIndex]);
            String md5String = EncryptUtil.encrypByMd5(jsonString);
//            suffix = String.valueOf(args[argIndex]);
            suffix = md5String;
        }

        key += ":" + suffix;
        valueKey += ":" + suffix;
        RLock redissonLock = redissonClient.getLock(key);

        try {
            if (redissonLock.isLocked()) {
                throw new OpenException("不允许重复提交");
            }
            if (!redissonLock.tryLock(30, TimeUnit.SECONDS)) {
                throw new OpenException("访问频繁请稍后再试");
            }

            logger.info("==================================================");
            for (Object arg : args) {
                logger.info(JSON.toJSONString(arg));
            }
            logger.info("==================================================");
            int seconds = noRepeatSubmit.seconds();
            logger.info("lock key : " + key);

            String value = stringRedisTemplate.opsForValue().get(valueKey);
            if (StringUtils.hasLength(value)) {
                throw new OpenException("不允许重复提交");
            }

            stringRedisTemplate.opsForValue().set(valueKey, "1", seconds, TimeUnit.SECONDS);

            Object proceed = joinPoint.proceed();
            return proceed;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (redissonLock.isLocked() && redissonLock.isHeldByCurrentThread()) {
                redissonLock.unlock();
            }
        }
    }

    public static Map<String, Object> getKeyAndValue(Object obj) {
        Map<String, Object> map = Maps.newHashMap();
        // 得到类对象
        Class userCla = (Class) obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            // 设置些属性是可以访问的
            f.setAccessible(true);
            Object val = new Object();
            try {
                val = f.get(obj);
                // 得到此属性的值
                // 设置键值
                map.put(f.getName(), val);
            } catch (IllegalArgumentException e) {
                logger.error("getKeyAndValue IllegalArgumentException", e);
            } catch (IllegalAccessException e) {
                logger.error("getKeyAndValue IllegalAccessException", e);
            }

        }
        logger.info("扫描结果：" + JSON.toJSONString(map));
        return map;
    }
}
