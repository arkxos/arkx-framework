package org.ark.framework.json;

import io.arkx.framework.commons.collection.Mapx;

/**
 * 
 * 
 * @author Darkness
 * @date 2013-3-30 下午03:56:18 
 * @version V1.0
 */
public class JSONObject extends Mapx<String, Object> implements JSONAware {
	
	private static final long serialVersionUID = -503443796854799292L;

	public JSONObject getJSONObject(String key) {
		return (JSONObject) get(key);
	}

	public JSONArray getJSONArray(String key) {
		return (JSONArray) get(key);
	}

	public boolean isNull(String key) {
		return containsKey(key);
	}

	public String toString() {
		return JSON.toJSONString(this);
	}

	public String toJSONString() {
		return JSON.toJSONString(this);
	}
}