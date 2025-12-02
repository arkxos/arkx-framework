package io.arkx.framework.commons.collection;

/**
 * 键值未找到事件监听器。<br>
 * 本监听器通过Mapx.setKeyNotFoundEventListener()方法设置到Mapx后，<br>
 * Mapx会在get()方法未获取到null并且键不存在时调用onKeyNotFound()，并将返回值加入到Mapx中
 *
 * @author Darkness
 * @date 2012-8-6 下午10:02:23
 * @version V1.0
 */
public interface KeyNotFoundEventListener<K, V> {
    public V onKeyNotFound(K key);
}
