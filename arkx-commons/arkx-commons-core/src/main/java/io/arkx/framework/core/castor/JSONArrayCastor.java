package io.arkx.framework.core.castor;

import io.arkx.framework.json.JSON;
import io.arkx.framework.json.JSONArray;

import java.util.Collection;

public class JSONArrayCastor extends AbstractCastor {
	
	private static JSONArrayCastor singleton = new JSONArrayCastor();

	public static JSONArrayCastor getInstance() {
		return singleton;
	}

	public boolean canCast(Class<?> type) {
		return (type.isAssignableFrom(Collection.class)) || (type == String.class) || (type.isArray());
	}

	public Object cast(Object obj, Class<?> type) {
		if (obj == null) {
			return null;
		}
		if ((obj instanceof JSONArray)) {
			return obj;
		}
		if (obj.getClass().isArray()) {
			Object[] os = (Object[]) obj;
			JSONArray arr = new JSONArray();
			for (int i = 0; i < os.length; i++) {
				arr.add(os[i]);
			}
			return arr;
		}
		if ((obj instanceof Collection)) {
			JSONArray arr = new JSONArray();
			Collection<?> c = (Collection) obj;
			arr.addAll(c);
			return arr;
		}
		String str = obj.toString();
		if ((str.startsWith("[")) && (str.endsWith("]"))) {
			return JSON.parseJSONArray(str);
		}
		return null;
	}
}
