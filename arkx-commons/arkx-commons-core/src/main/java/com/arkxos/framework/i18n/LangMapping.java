package com.arkxos.framework.i18n;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.arkxos.framework.Account;
import com.arkxos.framework.Config;
import com.arkxos.framework.Current;
import com.arkxos.framework.commons.collection.CacheMapx;
import com.arkxos.framework.commons.collection.ConcurrentMapx;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringFormat;

/**
 * 国际化字符串映射器
 * 
 */
public class LangMapping {
	protected ConcurrentMapx<String, ConcurrentMapx<String, String>> mapping = null;
	protected String defaultLanguage = "zh-cn";// 默认为中文，可以通过framework.xml配置DefaultLanguage选项
	protected CacheMapx<String, String> languageMap;
	private static LangMapping instance = null;
	private static ReentrantLock lock = new ReentrantLock();
	private static long lastTime = 0;

	public LangMapping() {
		String lang = System.getProperty("user.language");
		defaultLanguage = LangUtil.getLanguage(lang);
	}

	public static LangMapping getInstance() {
		if (lastTime == 0 || Config.isDebugMode() && System.currentTimeMillis() - lastTime > 3000) {// 开发模式下3秒扫描一次
			lock.lock();
			try {
				if (lastTime == 0 || Config.isDebugMode() && System.currentTimeMillis() - lastTime > 3000) {
					instance = LangLoader.load();
					lastTime = System.currentTimeMillis();
				}
			} finally {
				lock.unlock();
			}
		}
		return instance;
	}

	public Mapx<String, String> getLanguageMap() {
		return languageMap;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public Mapx<String, String> getAllValue(String lang) {
		return mapping.get(lang);
	}

	public static String get(String key, Object... args) {
		return getInstance().getValue(key, args);
	}

	public static String get(String lang, String key, Object... args) {
		return getInstance().getValue(lang, key, args);
	}

	public String getValue(String key, Object... args) {
		String lang = null;
		if (Current.getExecuteContext() != null) {
			lang = Current.getExecuteContext().getLanguage();
		} else {
			lang = Account.getLanguage();
		}
		if (ObjectUtil.isEmpty(lang)) {
			lang = defaultLanguage;
		}
		return getValue(lang, key, args);
	}

	public String getValue(String lang, String key, Object... args) {
		key = filterKey(key);
		Map<String, String> map = mapping.get(lang);
		if (map == null) {
			return null;
		}
		String str = map.get(key);
		if (str == null && !lang.equals(defaultLanguage)) {
			map = mapping.get(defaultLanguage);
			if (map != null) {
				str = map.get(key);
			}
		}
		if (str == null) {
			str = key;
		}
		if (ObjectUtil.notEmpty(args)) {
			str = StringFormat.format(str, args);
		}
		return str;
	}

	public void put(String lang, String key, String value) {
		key = filterKey(key);
		ConcurrentMapx<String, String> map = mapping.get(lang);
		if (map == null) {
			lock.lock();
			try {
				if (map == null) {
					map = new ConcurrentMapx<>();
					mapping.put(lang, map);
				}
			} finally {
				lock.unlock();
			}
		}
		map.put(key, value);
	}

	/**
	 * 如果key前缀为@{且后缀为}，则过滤之
	 */
	private String filterKey(String key) {
		if (key == null) {
			return key;
		}
		if (key.startsWith("@{") && key.endsWith("}")) {
			return key.substring(2, key.length() - 1);
		}
		return key;
	}
}
