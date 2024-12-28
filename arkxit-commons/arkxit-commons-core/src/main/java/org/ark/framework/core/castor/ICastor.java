package org.ark.framework.core.castor;

import com.rapidark.framework.extend.IExtendItem;

/**
 * 类型转换器
 * 
 * @author Darkness
 * @date 2013-3-26 下午07:28:04 
 * @version V1.0
 */
public interface ICastor extends IExtendItem {
	
	/**
	 * 是否为该转化器支持的类型
	 * 
	 * @author Darkness
	 * @date 2013-3-26 下午07:42:22 
	 * @version V1.0
	 */
	boolean canCast(Class<?> paramClass);

	/**
	 * 将传入的对象转换成该转换器的类型对象
	 * 
	 * @author Darkness
	 * @date 2013-3-26 下午07:42:27 
	 * @version V1.0
	 */
	Object cast(Object paramObject, Class<?> paramClass);
}