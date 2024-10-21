package org.ark.framework.jaf.spi;

import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.StringUtil;

/**
 *  @class org.ark.framework.jaf.spi.AliasMapping
 * Alias信息存储列表
 * 
 * @author Darkness
 * @date 2012-8-5 下午6:29:50 
 * @version V1.0
 */
public class AliasMapping {
	
	protected static Mapx<String, String> mapping = null;

	/**
	 * 根据名称获取mapping的类
	 * @param name 名称
	 * @return 名称对应的值
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午6:33:39 
	 * @version V1.0
	 */
	public static String get(String name) {
		AliasLoader.load();
		String str = mapping.getString(name);
		if ((str == null) && (StringUtil.count(name, ".") < 3)) {
			AliasLoader.lastTime = 0L;
			AliasLoader.load();
			str = mapping.getString(name);
		}
		return str;
	}

	/**
	 * 判断列表中是否存在指定的对象
	 * @param name 名称
	 * @return 列表中是否存在指定的对象
	 * @author Darkness
	 * @date 2012-8-5 下午6:34:38 
	 * @version V1.0
	 */
	public static boolean exists(String name) {
		AliasLoader.load();
		boolean flag = mapping.containsKey(name);
		if ((!flag) && (StringUtil.count(name, ".") < 3)) {
			AliasLoader.lastTime = 0L;
			AliasLoader.load();
			flag = mapping.containsKey(name);
		}
		return flag;
	}

	/**
	 * 将mapping信息放入列表
	 * @param key 键
	 * @param name 类名
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午6:35:29 
	 * @version V1.0
	 */
	protected static void put(String key, String name) {
		mapping.put(key, name);
	}
}