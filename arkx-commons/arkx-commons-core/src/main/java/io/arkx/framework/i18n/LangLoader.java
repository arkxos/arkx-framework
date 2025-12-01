package io.arkx.framework.i18n;

import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.CacheMapx;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.PropertiesUtil;
import io.arkx.framework.config.DefaultLanguage;
import io.arkx.framework.extend.plugin.PluginConfig;
import io.arkx.framework.extend.plugin.PluginManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 语言文件加载器
 * 
 */
public class LangLoader {

	public static LangMapping load() {
		String path = Config.getWEBINFPath();
		if (new File(path).exists()) {
			LangMapping lm = loadMapping(path);
			String lang = DefaultLanguage.getValue();
			if (ObjectUtil.notEmpty(lang)) {
				lm.defaultLanguage = lang;
			}
			return lm;
		} else {
			return new LangMapping();
		}
	}

	/**
	 * 读取指定应用下的国际化资源文件,path参数应该一个UI/WEB-INF/目录
	 */
	public static LangMapping loadMapping(String path) {
		LangMapping lm = new LangMapping();

		File langFile = new File(path + "/classes/lang/lang.i18n");
		if (langFile.exists()) {
			Mapx<String, String> map = PropertiesUtil.read(langFile);
			lm.languageMap = new CacheMapx<>();
			lm.languageMap.putAll(map);
		} else {
			lm.languageMap = new CacheMapx<>();
			lm.languageMap.put("zh-cn", "中文(简体)");
		}
		for (PluginConfig pc : PluginManager.getInstance().getAllPluginConfig()) {// 此处得到的列表是按依赖关系排序之后的
			loadFromJar(LangLoader.class.getResourceAsStream("/lang/" +pc.getID() + "/zh-cn.i18n"), lm);
//			loadFromClasses(LangLoader.class.getResourceAsStream("/lang/" +pc.getID()), lm);
		}
		return lm;
	}

	private static void loadFromJar(InputStream inputStream, LangMapping lm) {
//		if (!f.exists()) {
//			return;
//		}
		if(inputStream == null) {
			return;
		}
//		if (f.getName().endsWith(".jar")) {
			try {
//				Mapx<String, Long> files = ZipUtil.getFileListInZip(f.getAbsolutePath());
//				for (String fileName : files.keySet()) {
//					if (fileName.endsWith(".i18n")) {
//						byte[] bs = ZipUtil.readFileInZip(f.getAbsolutePath(), fileName);
						byte[] bs = FileUtil.readByte(inputStream);
//						int start = fileName.indexOf("/") >= 0 ? fileName.lastIndexOf("/") + 1 : 0;
//						int end = fileName.lastIndexOf(".");
//						String lang = fileName.substring(start, end);
						Mapx<String, String> map = PropertiesUtil.read(new ByteArrayInputStream(bs));
//						if (fileName.endsWith("/lang.i18n")) {
//							lm.languageMap = new CacheMapx<String, String>();
//							lm.languageMap.putAll(map);
//						} else {
							for (String key : map.keySet()) {
//								lm.put(lang, key, map.get(key));
								lm.put("zh-cn", key, map.get(key));
							}
//						}
//					}
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
//		}
	}

	private static void loadFromClasses(File f, LangMapping lm) {
		if (!f.exists()) {
			return;
		}
		try {
			for (File f2 : f.listFiles()) {
				if (f2.isFile() && f2.getName().toLowerCase().endsWith(".i18n")) {
					Mapx<String, String> map = PropertiesUtil.read(f2);
					for (String key : map.keySet()) {
						String lang = f2.getName().substring(0, f2.getName().lastIndexOf("."));
						lm.put(lang, key, map.get(key));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
