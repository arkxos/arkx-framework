package io.arkx.framework.core.castor;

import java.util.Map;

import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.json.JSON;
import io.arkx.framework.json.JSONObject;

public class JSONObjectCastor extends AbstractCastor {

	private static JSONObjectCastor singleton = new JSONObjectCastor();

	public static JSONObjectCastor getInstance() {
		return singleton;
	}

	public boolean canCast(Class<?> type) {
		return (type.isAssignableFrom(Map.class)) || (type == String.class);
	}

	public Object cast(Object obj, Class<?> type) {
		if (obj == null) {
			return null;
		}
		if ((obj instanceof JSONObject)) {
			return obj;
		}
		if ((obj instanceof Map)) {
			JSONObject jo = new JSONObject();
			Map<?, ?> map = (Map) obj;
			jo.putAll(ObjectUtil.toStringObjectMap(map));
			return jo;
		}
		String str = obj.toString();
		if ((str.startsWith("{")) && (str.endsWith("}"))) {
			return JSON.parseJSONObject(str);
		}
		return null;
	}

}
