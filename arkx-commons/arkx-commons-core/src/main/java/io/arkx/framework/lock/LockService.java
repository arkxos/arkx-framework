package io.arkx.framework.lock;

/**
 * @author darkness
 * @date 2021/7/12 14:30
 * @version 1.0
 */
public interface LockService {

    void unLock(String key);

    boolean isLock(String key, int seconds);

    void unLock(String key, String value);

    <T> T lockExecute(String key, LockExecute<T> lockExecute);

    interface LockExecute<T> {
        T execute();

        T waitTimeOut();
    }
}
