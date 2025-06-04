package com.arkxos.framework.i18n;


/**
 * LangMapping.get()的缩写
 * 
 */
public class Lang {
	public static String get(String key, Object... args) {
		return LangMapping.get(key, args);
	}

	public static String get(String lang, String key, Object... args) {
		return LangMapping.get(lang, key, args);
	}
}
