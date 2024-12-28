package org.ark.framework.json.convert;

import org.ark.framework.json.JSONObject;

import com.rapidark.framework.extend.IExtendItem;

/**
 * 
 * 
 * @author Darkness
 * @date 2013-3-30 下午03:56:41 
 * @version V1.0
 */
public interface IJSONConvertor extends IExtendItem {
	
	String getTypeID();

	boolean match(Object paramObject);

	JSONObject toJSON(Object paramObject);

	Object fromJSON(JSONObject paramJSONObject);
}