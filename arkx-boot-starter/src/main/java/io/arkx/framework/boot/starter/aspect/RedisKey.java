package io.arkx.framework.boot.starter.aspect;

/**
 * 锁前缀类
 * @author darkness
 * @date 2021/7/12 14:28
 * @version 1.0
 */
public class RedisKey {
    /**
     *  不可重复点击的锁前缀
     */
    public static final String NO_REPEAT_LOCK_PREFIX = "rapidark:no_repeat_lock:";

    /**
     *  不可重复点击的锁前缀
     */
    public static final String NO_REPEAT_LOCK_VALUE_PREFIX = "rapidark:no_repeat_lock_value:";
}
