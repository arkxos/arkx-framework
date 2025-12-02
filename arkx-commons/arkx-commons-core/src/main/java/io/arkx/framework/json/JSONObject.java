package io.arkx.framework.json;

import io.arkx.framework.commons.collection.Mapx;

import com.alibaba.fastjson.JSONAware;

/**
 * JSON对象，继承Mapx<String,Object>
 *
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

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	@Override
	public String toJSONString() {
		return JSON.toJSONString(this);
	}

}
