package com.arkxos.framework.data.oldfastdb;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author Darkness
 * @date 2015年12月19日 下午7:16:04
 * @version V1.0
 * @since infinity 1.0
 */
public class FastTableConvertRegister {

	private static Map<Class<? extends IFastTable>, IFastTableConvertor<? extends IFastTable>> map = new HashMap<>();

	static {
		map.put(FastDataTable.class, new FastDataTableConvertor());
	}

	@SuppressWarnings("unchecked")
	public static <T extends IFastTable> IFastTableConvertor<T> get(Class<T> lightningTableType) {
		IFastTableConvertor<? extends IFastTable> convertor = map.get(lightningTableType);
		if(convertor != null) {
			return (IFastTableConvertor<T>)convertor;
		}
		
		for (Class<? extends IFastTable> key : map.keySet()) {
			if(key.isAssignableFrom(lightningTableType)) {
				return (IFastTableConvertor<T>)map.get(key);
			}
		}
		
		return null;
	}
}
