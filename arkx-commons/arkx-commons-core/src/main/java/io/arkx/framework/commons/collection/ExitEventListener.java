package io.arkx.framework.commons.collection;

/**
 * @class org.ark.framework.collection.ExitEventListener
 *        清除事件监听器，可以为Mapx等容器设置清除事件监听器，<br>
 *        当容器中有元素被清除时会调用监听器的onExit方法。
 *
 * @author Darkness
 * @date 2012-8-6 下午10:00:12
 * @version V1.0
 */
public abstract class ExitEventListener<K, V> {
    /**
     * 键值对从容器中清除时会调用此方法
     *
     * @param key
     * @param value
     */
    public abstract void onExit(K key, V value);

}
