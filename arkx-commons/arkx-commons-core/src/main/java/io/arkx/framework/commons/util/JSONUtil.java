package io.arkx.framework.commons.util;

import java.util.Map;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.json.JSON;
import io.arkx.framework.json.JSONArray;
import io.arkx.framework.json.JSONObject;

/**
 * @class org.ark.framework.utility.JSONUtil json对象转换工具类
 * @author Darkness
 * @date 2012-8-6 下午9:50:33
 * @version V1.0
 */
public class JSONUtil {

	public static Mapx<String, Object> toMap(String json) {
		Object obj = JSON.parse(json);
		if (!(obj instanceof JSONObject)) {
			throw new RuntimeException("不是JSON对象:" + json);
		}
		JSONObject jo = (JSONObject) obj;
		return toMap(jo);
	}

	private static Mapx<String, Object> toMap(JSONObject jo) {
		Mapx<String, Object> map = new Mapx();
		for (Object k : jo.keySet()) {
			Object v = jo.get(k);
			String key = String.valueOf(k);
			if ((v instanceof JSONArray)) {
				v = ((JSONArray) v).toArray();
			}
			else if ((v instanceof JSONObject)) {
				v = toMap((JSONObject) v);
			}
			map.put(key, v);
		}
		return map;
	}

	public static String toJSON(Map<?, ?> map) {
		return JSON.toJSONString(map);
	}

	public static <T> T toPOJO(String json, Class<T> clazz) throws Exception {
		Object obj = JSON.parseBean(json, clazz);
		return (T) obj;
	}

}
