package com.rapidark.framework.cache;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.rapidark.framework.commons.collection.CacheMapx;
import com.rapidark.framework.extend.IExtendItem;

/**
 * 缓存提供者抽象类，使用方式如下：<br/>
 * package org.ark.platform.pub;<br/>
 * public class PlatformCache extends CacheProvider {}<br/>
 * <br/>
 * 在.plugin文件下加上如下配置：<br/>
 * id、class指向当前缓存提供者<br/>
 * &lt;extendItem&gt;<br/>
    &nbsp;&nbsp;&nbsp;&nbsp;&lt;id>org.ark.platform.pub.PlatformCache&lt;/id><br/>
    &nbsp;&nbsp;&nbsp;&nbsp;&lt;class>org.ark.platform.pub.PlatformCache&lt;/class><br/>
    &nbsp;&nbsp;&nbsp;&nbsp;&lt;description>&lt;/description><br/>
    &nbsp;&nbsp;&nbsp;&nbsp;&lt;extendService>org.ark.framework.cache.CacheService&lt;/extendService><br/>
  &lt;/extendItem><br/>
 * 
 * @author Darkness
 * @date 2013-1-31 上午11:11:42 
 * @version V1.0
 */
public abstract class CacheDataProvider implements IExtendItem {
	
	protected Lock Lock = new ReentrantLock();
	public CacheMapx<String, CacheMapx<String, Object>> TypeMap = new CacheMapx<>();
	protected boolean OnNotFound = false;// 表明当前处于onKeyNotFound,OnTypeNotFound调用期间

	/**
	 * 当缓冲数据项置入时调用此方法。<br>
	 * 同一份数据在多个子类型中有键值时可以通过覆盖本方法避免多次载入，以提高性能。
	 * 
	 * @param type 子类型
	 * @param key 数据项键
	 * @param value 数据项值
	 */
	public void onKeySet(String type, String key, Object value) {
	}

	/**
	 * 当某个缓存子类型没有找到时调用此方法。<br>
	 * 子类通过实现此方法将一个子类型下的所有数据项一次性载入缓存。
	 * 
	 * @param type 子类型
	 */
	public abstract void onTypeNotFound(String type);

	/**
	 * 当某个数据项没有找到时调用此方法。<br>
	 * 子类通过实现本方法达到缓存加载数据项的效果。
	 * 
	 * @param type 子类型
	 * @param key 数据项键
	 */
	public abstract void onKeyNotFound(String type, String key);

	/**
	 * 销毁缓存数据
	 */
	public void destory() {
		for (CacheMapx<String, Object> map : TypeMap.values()) {
			map.clear();
			map = null;
		}
		TypeMap.clear();
		TypeMap = null;
	}
}
