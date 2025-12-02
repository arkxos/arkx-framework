package org.ark.framework.jaf.spi;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import io.arkx.framework.Config;
import io.arkx.framework.annotation.Alias;
import io.arkx.framework.annotation.Priv;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.*;
import io.arkx.framework.cosyui.web.UIFacade;
import io.arkx.framework.data.xml.XMLElement;
import io.arkx.framework.i18n.LangUtil;

/**
 * @class org.ark.framework.jaf.spi.AliasLoader 类信息加载器
 * @author Darkness
 * @date 2012-8-5 下午4:51:04
 * @version V1.0
 */
public class AliasLoader {

	protected static long lastTime = 0L;

	private static Object mutex = new Object();

	/**
	 * debug模式下，超过3秒再次调用会重新初始化
	 */
	private static boolean needLoad() {
		return (lastTime == 0L) || ((Config.isDebugMode()) && (System.currentTimeMillis() - lastTime > 3000L));
	}

	/**
	 * 加载类标注信息：
	 *
	 * 1、扫描所有"UI.class"结尾的类到AliasMapping.mapping中
	 *
	 * 扫描范围&顺序： 1、lib文件夹下的包含“-plugin-”的jar 2、web-inf/classes下的类
	 *
	 * 忽略对象："com.ark.schema"，"org.ark.framework"包下的对象忽略
	 *
	 * 2、将配置文件中配置的“*.mapping.method”（配置方式：{id, value}）添加到AliasMapping.mapping中
	 * 3、将配置文件中配置的“*.mapping.sql”（配置方式：{id, value}）添加到SQLMapping.mapping中
	 *
	 * @author Darkness
	 * @date 2012-8-5 下午6:10:03
	 * @version V1.0
	 */
	public static void load() {
		if (needLoad())
			synchronized (mutex) {
				if (needLoad()) {
					AliasMapping.mapping = new Mapx<>();
					SQLMapping.mapping = new Mapx<>();

					File f = new File(Config.getContextRealPath() + "WEB-INF/cache/.alias");
					if ((Config.isDebugMode()) && (f.exists()) && (f.length() > 0L)) {
						AliasMapping.mapping = PropertiesUtil.read(f);
					}
					else {
						scanAllAnnotation();
					}
					Config.reloadXmlConfig();

					List<XMLElement> nds = Config.getElements("*.mapping.method");
					for (int i = 0; (nds != null) && (i < nds.size()); i++) {
						String id = nds.get(i).getAttributes().getString("id");
						String value = nds.get(i).getAttributes().getString("value");
						AliasMapping.mapping.put(id, value);
					}

					nds = Config.getElements("*.mapping.sql");
					for (int i = 0; (nds != null) && (i < nds.size()); i++) {
						String id = nds.get(i).getAttributes().getString("id");
						String value = nds.get(i).getAttributes().getString("value");
						SQLMapping.mapping.put(id, value);
					}
					lastTime = System.currentTimeMillis();
				}
			}
	}

	/**
	 * 扫描所有"UI.class"结尾的类
	 *
	 * 扫描范围&顺序： 1、lib文件夹下的包含“-plugin-”的jar 2、web-inf/classes下的类
	 *
	 * 忽略对象："org.ark.framework"包下的对象忽略
	 *
	 * @author Darkness
	 * @date 2012-8-5 下午5:33:34
	 * @version V1.0
	 */
	private static void scanAllAnnotation() {
		long t = System.currentTimeMillis();

		loadPath(new File(Config.getClassesPath()).getParentFile().getAbsolutePath() + File.separator);
		loadPath(Config.getPluginPath());

		if (lastTime == 0L) {
			LogUtil.info("----" + Config.getAppCode() + "(" + LangUtil.get(Config.getAppName()) + "): Scan Alias used "
					+ (System.currentTimeMillis() - t) + " ms----");
		}
		lastTime = System.currentTimeMillis();
	}

	private static void loadPath(String path) {
		try {
			File p = new File(path + "lib/");
			if (p.exists()) {
				File[] fs = p.listFiles();
				for (File f : fs) {
					if (f.getName().indexOf("-plugin-") < 0)
						continue;
					if (!f.getName().endsWith(".jar")) {
						continue;
					}
					try {
						Mapx<String, Long> files = ZipUtil.getFileListInZip(f.getAbsolutePath());
						for (String fileName : files.keyArray())
							if (fileName.endsWith("UI.class")) {
								String className = fileName.substring(0, fileName.lastIndexOf("."));
								className = StringUtil.replaceEx(className, "/", ".");
								if ((!className.startsWith("com.ark.schema"))
										&& (!className.startsWith("org.ark.framework")))
									scanOneClass(className);
							}
					}
					catch (Exception e) {
						e.printStackTrace();
					}

				}

			}

			p = new File(path + "classes/");
			if (p.exists())
				scanOneDir(p, path + "classes/");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 扫描所有"UI.class"结尾的类
	 *
	 * 忽略对象： 1、"org.ark.framework"包下的对象忽略 2、"App.ExcludeAliasScan"配置中排除的目录，排除目录用“，”分隔
	 *
	 * @author Darkness
	 * @date 2012-8-5 下午5:58:39
	 * @version V1.0
	 */
	private static void scanOneDir(File p, String prefix) throws ClassNotFoundException {
		String classPath = Config.getClassesPath();
		String path = FileUtil.normalizePath(p.getAbsolutePath());
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		path = path.substring(classPath.length());
		if ((path.equals("com/ark/schema")) || (path.equals("com/ark/framework"))) {
			return;
		}
		String exclude = Config.getValue("App.ExcludeAliasScan");
		if (ObjectUtil.notEmpty(exclude)) {
			String[] arr = StringUtil.splitEx(exclude, ",");
			for (String str : arr) {
				if ((ObjectUtil.notEmpty(str)) && (path.startsWith(str))) {
					return;
				}
			}
		}
		File[] fs = p.listFiles();
		for (int i = 0; i < fs.length; i++) {
			File f = fs[i];
			if (f.isFile()) {
				if (f.getName().endsWith("UI.class")) {
					String name = f.getAbsolutePath().substring(prefix.length());
					name = name.replaceAll("[\\/\\\\]+", ".");
					name = name.substring(0, name.length() - 6);
					scanOneClass(name);
				}
			}
			else
				scanOneDir(f, prefix);
		}
	}

	/**
	 * 扫描继承自UIFacade的类中所有标注了@Priv注解的public方法，将符合规则的Alias存入AliasMapping列表中
	 *
	 * 扫描提取规则： 1、方法没有标注@Alias注解的，类标注了@Alias注解， 将{类别名.方法名： 类名.方法名}存入AliasMapping列表中
	 *
	 * 2、方法标注了@Alias注解， 将{[类别名.]方法别名： 类名.方法名}存入AliasMapping列表中
	 *
	 * @author Darkness
	 * @date 2012-8-5 下午5:40:05
	 * @version V1.0
	 */
	public static void scanOneClass(String className) {
		Class<?> c = null;
		try {
			// if(!className.startsWith("org") ) {
			// return;
			// }
			c = Class.forName(className);
		}
		catch (Throwable t) {
			t.printStackTrace();
			LogUtil.error("Load class failed:" + className);
		}
		if (!UIFacade.class.isAssignableFrom(c)) {
			return;
		}
		String classAlias = null;
		if (c.isAnnotationPresent(Alias.class)) {
			classAlias = c.getAnnotation(Alias.class).value();
		}
		for (Method m : c.getMethods()) {
			if (!Modifier.isPublic(m.getModifiers())) {
				continue;
			}
			if (!m.isAnnotationPresent(Priv.class)) {
				continue;
			}
			if ((classAlias != null) && (!m.isAnnotationPresent(Alias.class))) {
				AliasMapping.put(classAlias + "." + m.getName(), c.getName() + "." + m.getName());
			}
			else {
				Alias named = m.getAnnotation(Alias.class);
				if (named != null) {
					String methodAlias = named.value();
					if (classAlias != null) {
						methodAlias = classAlias + "." + methodAlias;
					}
					AliasMapping.put(methodAlias, c.getName() + "." + m.getName());
				}
			}
		}
	}

}
