package org.ark.framework.json;

import java.util.ArrayList;

import org.ark.framework.core.castor.IntCastor;
import org.ark.framework.core.castor.LongCastor;

/**
 *
 *
 * @author Darkness
 * @date 2013-3-30 下午03:56:02
 * @version V1.0
 */
public class JSONArray extends ArrayList<Object> implements JSONAware {

    private static final long serialVersionUID = 3957988303675231981L;

    public String toString() {
        return JSON.toJSONString(this);
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    public JSONObject getJSONObject(int i) {
        return (JSONObject) get(i);
    }

    public Long getLong(int i) {
        return (Long) LongCastor.getInstance().cast(get(i), null);
    }

    public Integer getInt(int i) {
        return (Integer) IntCastor.getInstance().cast(get(i), null);
    }

    public String getString(int i) {
        Object obj = get(i);
        return obj == null ? null : obj.toString();
    }

    public int length() {
        return size();
    }
}
