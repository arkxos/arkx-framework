package io.arkx.framework.commons.collection;

/**
 * 线程安全并且键值有顺序的Mapx
 * 
 */
public class ConcurrentMapx<K, V> extends Mapx<K, V> {
	private static final long serialVersionUID = 201404182133L;

	public ConcurrentMapx() {
		super(true);
	}

	public ConcurrentMapx(int initCapacity) {
		super(initCapacity, true);
	}
}
