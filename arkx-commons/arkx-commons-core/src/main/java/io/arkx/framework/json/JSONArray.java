package io.arkx.framework.json;

import java.util.ArrayList;

import io.arkx.framework.core.castor.LongCastor;

import com.alibaba.fastjson.JSONAware;

/**
 * JSON数组,继承ArrayList<Object>
 *
 */
public class JSONArray extends ArrayList<Object> implements JSONAware {

	private static final long serialVersionUID = 3957988303675231981L;

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	@Override
	public String toJSONString() {
		return JSON.toJSONString(this);
	}

	public JSONObject getJSONObject(int i) {
		return (JSONObject) get(i);
	}

	public long getLong(int i) {
		return (Long) LongCastor.getInstance().cast(get(i), null);
	}

	public String getString(int i) {
		Object obj = get(i);
		return obj == null ? null : obj.toString();
	}

	public int length() {
		return size();
	}

	public JSONArray getJSONArray(int i) {
		return (JSONArray) get(i);
	}

}
