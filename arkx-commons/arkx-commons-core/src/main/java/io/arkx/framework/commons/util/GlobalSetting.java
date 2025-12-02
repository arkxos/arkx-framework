package io.arkx.framework.commons.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * GlobalSetting.set("name", "darkenss");
 *
 * String name = GlobalSetting.get("name"); assert("darkenss".equals(name));
 *
 * @author Administrator
 *
 */
public class GlobalSetting {

	private static ConcurrentSkipListMap<String, String> settings = new ConcurrentSkipListMap<>();

	private static final String ROOT = System.getProperty("user.home");

	private static String PATH = ROOT + File.separator + "config";

	public static void setPath(String path) {
		PATH = path;
	}

	private static Object syncObject = new Object();

	private static boolean inited = false;

	private static String filePath() {
		return PATH + File.separator + "golbalSetting.properties";
	}

	private static void init() {
		if (inited) {
			return;
		}

		synchronized (syncObject) {
			if (inited) {
				return;
			}

			File configFolder = new File(PATH);
			if (!configFolder.exists()) {
				configFolder.mkdirs();
			}
			File settingFile = new File(filePath());
			if (!settingFile.exists()) {
				try {
					settingFile.createNewFile();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}

			Map<String, String> map = PropertiesUtil.read(settingFile);
			settings.putAll(map);

			inited = true;
		}
	}

	public static String get(String key) {
		init();

		return settings.get(key);
	}

	public static boolean getBoolean(String key) {
		String value = get(key);
		if (StringUtil.isEmpty(value)) {
			return false;
		}

		return Boolean.valueOf(value);
	}

	public static LocalDate getLocalDate(String key) {
		String value = get(key);
		if (StringUtil.isEmpty(value)) {
			return null;
		}

		return LocalDate.parse(value);
	}

	public static LocalDate getLocalDate(String key, LocalDate defaultValue) {
		init();

		String value = settings.get(key);
		if (StringUtil.isEmpty(value)) {
			return defaultValue;
		}

		return LocalDate.parse(value);
	}

	public static String get(String key, String defaultValue) {
		init();

		String value = settings.get(key);
		if (StringUtil.isEmpty(value)) {
			value = defaultValue;
		}

		return value;
	}

	public static String set(String key, String value) {
		init();

		String oldValue = settings.put(key, value);

		File settingFile = new File(filePath());
		PropertiesUtil.write(settingFile, settings);

		return oldValue;
	}

}
