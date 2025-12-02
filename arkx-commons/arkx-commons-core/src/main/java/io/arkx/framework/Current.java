package io.arkx.framework;

import java.util.Map;

import io.arkx.framework.commons.collection.Mapx;

/**
 * @author Nobody
 * @date 2025-06-04 20:33
 * @since 1.0
 */
public class Current {

	/**
	 * 各线程数据分离
	 */
	protected static ThreadLocal<CurrentData> current = new ThreadLocal<>();

	/**
	 * 设置线程上下文有效的变量
	 */
	public static void put(String key, Object value) {
		CurrentData data = current.get();
		if (data == null) {
			data = new CurrentData();
			data.values = new Mapx<>();
			current.set(data);
		}
		else if (data.values == null) {
			data.values = new Mapx<>();
		}
		if (value instanceof Map) {
			Map<?, ?> vmap = (Map<?, ?>) value;
			for (Object k : vmap.keySet()) {
				data.values.put(key + "." + k, vmap.get(k));
			}
		}
		data.values.put(key, value);
	}

	/**
	 * 获得线程上下文有效的变量
	 */
	public static Object get(String key) {
		CurrentData data = current.get();
		if (data == null) {
			return null;
		}
		return data.values.get(key);
	}

	/**
	 * 获得线程上下文有效的所有变量
	 */
	public static Map<String, Object> getValues() {// NO_UCD
		CurrentData data = current.get();
		if (data == null) {
			return null;
		}
		return data.values;
	}

	/**
	 * 清除当前数据
	 */
	public static void clear() {
		if (current.get() != null) {
			current.get().clear();
		}
	}

}
