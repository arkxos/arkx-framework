package com.arkxos.framework.json.convert;

import com.arkxos.framework.extend.IExtendItem;
import com.arkxos.framework.json.JSONObject;

/**
 * JSON转换器接口
 * 
 */
public interface IJSONConvertor extends IExtendItem {
	/**
	 * 是否处理当前对象
	 */
	boolean match(Object obj);

	/**
	 * 将指定对象输出成JSON
	 */
	JSONObject toJSON(Object obj);

	/**
	 * 将JSON解析后的Map转化成相应类型的对象
	 */
	Object fromJSON(JSONObject map);
}
