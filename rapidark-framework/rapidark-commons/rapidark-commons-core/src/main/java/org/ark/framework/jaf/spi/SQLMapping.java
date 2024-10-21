package org.ark.framework.jaf.spi;

import com.rapidark.framework.commons.collection.Mapx;

/**
 * @class org.ark.framework.jaf.spi.SQLMapping
 * sql mapping 集合
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:36:39 
 * @version V1.0
 */
public class SQLMapping {
	protected static Mapx<String, String> mapping = null;

	public static String get(String name) {
		AliasLoader.load();
		return mapping.getString(name);
	}

	protected static void put(String key, String str) {
		mapping.put(key, str);
	}
}