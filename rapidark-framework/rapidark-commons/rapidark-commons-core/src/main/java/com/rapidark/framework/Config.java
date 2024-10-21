package com.rapidark.framework;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.rapidark.framework.Account.UserData;
import com.rapidark.framework.commons.collection.ConcurrentMapx;
import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.LogUtil;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.cosyui.web.mvc.SessionListener;
import com.rapidark.framework.data.db.connection.ConnectionPoolManager;
import com.rapidark.framework.data.xml.XMLElement;
import com.rapidark.framework.i18n.LangUtil;
import com.rapidark.framework.security.EncryptUtil;

/**
import com.rapidark.preloader.facade.HttpSessionListenerFacade;
 * 全局配置信息类。<br>
 *  @singleton
 * @author Darkness
 * @date 2012-8-5 下午2:34:51 
 * @version V1.0
 */
public class Config {
	
	private static String[] configPaths;
	
	public static void withNormalMode() {
		configPaths = new String[] { Config.getClassesPath() };
	}

	public static void withPluginMode() {
		configPaths = new String[] { Config.getPluginPath() + "classes/" };
	}

	public static void withTestMode() {
		configPaths = new String[]{
			Config.getPluginPath() + "classes/",
			Config.getPluginPath() + "test-classes/"
		};
	}
	
	/**
	 * 保存各配置项的Map
	 */
	protected static ConcurrentMapx<String, String> configMap = new ConcurrentMapx<>();

	/**
	 * 数据库是否己配置
	 */
	protected static boolean isInstalled = false;

	/**
	 * 是否临时禁止登录
	 */
	protected static boolean isAllowLogin = true;

	/**
	 * 是否运行在一个插件环境下
	 */
	protected static boolean isPluginContext = false;

	/**
	 * 应用代码
	 */
	protected static String appCode = null;

	/**
	 * 应用名称
	 */
	protected static String appName = null;

	/**
	 * 是否是复杂部署模式，复杂部署模式需要考虑到一个应用有多个路径的问题，例如内外网不同的访问路径
	 */
	protected static boolean isComplexDepolyMode = false;

	/**
	 * 是否是前置部署，前置部署是将非后台的功能单独部署
	 */
	protected static boolean isFrontDeploy = false;

	/**
	 * Servlet容器支持的JSP规范的最大版本
	 */
	protected static int servletMajorVersion;

	/**
	 * Servlet容器支持的JSP规范的最小版本
	 */
	protected static int servletMinorVersion;

	/**
	 * 全局字符集设置，在读写文本、与数据库通信等所有涉及到字符串但又未明确指定字符集的地方会使用全局字符集进行操作
	 */
	protected static String globalCharset;

	/**
	 * classes目录的全路径
	 */
	protected static String classesPath;
	/**
	 * 调试模式标识
	 */
	protected static Boolean isDebugMode;

	private static ConfigLoader configLoader;
	
	/**
	 * 初始化配置项
	 * @method init
	 * @private
	 * @author Darkness
	 * @date 2012-8-9 上午10:27:15 
	 * @version V1.0
	 */
	protected static void init() {
		if (!configMap.containsKey("System.JavaVersion")) {
			if(configPaths == null) {
				withPluginMode();
			}
			
			configLoader = new ConfigLoader(configPaths);
			configLoader.load(Config.class.getResourceAsStream("/framework.xml"));
			configLoader.load(Config.class.getResourceAsStream("/database.xml"));
//			configLoader.load();
			
			configMap.put("App.ContextRealPath", Config.getContextRealPath());
			configMap.put("System.JavaVersion", System.getProperty("java.version"));
			configMap.put("System.JavaVendor", System.getProperty("java.vendor"));
			configMap.put("System.JavaHome", System.getProperty("java.home"));
			configMap.put("System.OSPatchLevel", System.getProperty("sun.os.patch.level"));// 其他JDK以后补充
			configMap.put("System.OSArch", System.getProperty("os.arch"));
			configMap.put("System.OSVersion", System.getProperty("os.version"));
			configMap.put("System.OSName", System.getProperty("os.name"));
			
			String osName = System.getProperty("os.name");
			boolean isWindows = osName.toLowerCase().indexOf("windows") > 0;
			if (isWindows && osName.equals("6.1")) {
				configMap.put("System.OSName", "Windows 7");
			}
			
			configMap.put("System.OSUserLanguage", System.getProperty("user.language"));
			configMap.put("System.OSUserName", System.getProperty("user.name"));
			configMap.put("System.LineSeparator", System.getProperty("line.separator"));
			configMap.put("System.FileSeparator", System.getProperty("file.separator"));
			configMap.put("System.FileEncoding", System.getProperty("file.encoding"));

			List<XMLElement> datas = configLoader.getElements("framework.application.config");
			if (datas == null) {
				LogUtil.warn("File framework.xml not found");
				isInstalled = false;
				return;
			}
			for (XMLElement data : datas) {
				configMap.put("App." + data.getAttributes().get("name"), data.getText());
			}
			datas = configLoader.getElements("*.allowUploadExt.config");
			for (XMLElement data : datas) {
				configMap.put(data.getAttributes().get("name"), data.getText());
			}
			datas = configLoader.getElements("data.config");
			for (XMLElement data : datas) {
				configMap.put(data.getAttributes().get("name"), data.getAttributes().get("value"));
			}
			isComplexDepolyMode = "true".equals(configMap.get("App.ComplexDepolyMode"));
			isFrontDeploy = "true".equals(configMap.get("App.FrontDeploy"));

			datas = configLoader.getElements("framework.databases.database");
			for (XMLElement data : datas) {
				String dbname = data.getAttributes().get("name");
				List<XMLElement> children = data.elements();
				for (int k = 0; k < children.size(); k++) {
					String attr = children.get(k).getAttributes().get("name");
					String value = children.get(k).getText();
					if (attr.equalsIgnoreCase("Password")) {
						if (value.startsWith("$KEY")) {// 以下是临时写法，以兼容以前的版本
							value = EncryptUtil.decrypt3DES(value.substring(4), EncryptUtil.DEFAULT_KEY);
						}
					}
					
					//车享 从UCM统一配置中心读取数据库配置信息
//					if (value.startsWith(UCMConfig.CONFIGKEY)) {
//						value = UCMConfig.getValue(value.substring(UCMConfig.CONFIGKEY.length()+1));
//					}
					
					configMap.put("Database." + dbname + "." + attr, value);
				}
			}
			if (datas.size() > 0) {
				isInstalled = true;
			} else {
				isInstalled = false;
			}
			LogUtil.info("----" + Config.getAppCode() + "(" + Config.getAppName() + "): Config Initialized----");
		}
	}

	/**
	 * 重新载入所有全局配置项
	 * @method loadConfig
	 * @author Darkness
	 * @date 2012-8-9 上午10:27:15 
	 * @version V1.0
	 */
	public static void loadConfig() {
		configMap.remove("System.JavaVersion");
		init();
	}

	/**
	 * 所有全局配置项组成的Mapx
	 * @method getMapx
	 * @return {Mapx<String,String>}
	 * @author Darkness
	 * @date 2012-8-9 上午10:27:15 
	 * @version V1.0
	 */
	public static Mapx<String, String> getMapx() {
		return configMap;
	}

	/**
	 * @return 是否是前置部署
	 */
	public static boolean isFrontDeploy() {
		return isFrontDeploy;
	}

	/**
	 * @return 导出Excel时使用的默认版本
	 */
	public static String getExcelVersion() {
		String ev = getValue("App.ExcelVersion");
		if (StringUtil.isEmpty(ev)) {
			ev = "2007"; // 默认2007
		}
		return ev;
	}

	/**
	 * 返回配置项的值，XML配置文件中的framework/application/config节点中的配置项名称必须使用“App.”前缀访问。
	 * 
	 * @param configName 配置项名称
	 * @return 配置项的值
	 * 
	 * @method getValue
	 * @param {String} configName 配置项名称
	 * @return {String}
	 * @author Darkness
	 * @date 2012-8-9 上午10:27:15 
	 * @version V1.0
	 */
	public static String getValue(String configName) {
		init();
		return configMap.get(configName);
	}

	/**
	 * 设置配置项的值
	 * 
	 * @method setValue
	 * @param {String} configName 配置项名称
	 * @param {String} configValue 配置项值
	 * @author Darkness
	 * @date 2012-8-9 上午10:27:15 
	 * @version V1.0
	 */
	public static void setValue(String configName, String configValue) {
		init();
		configMap.put(configName, configValue);
	}

	/**
	 * 获取系统WEB-INF所在目录的全路径
	 * @method getWEBINFPath
	 * @return {String}
	 * @author Darkness
	 * @date 2012-8-9 上午10:27:15 
	 * @version V1.0
	 */
	public static String getWEBINFPath() {
		String path = getClassesPath();
		String webInf = "WEB-INF";
		
		if (path.indexOf(webInf) > 0) {
			return path.substring(0, path.lastIndexOf(webInf) + webInf.length() + 1);
		}
		return path;
	}

	/**
	 * J2EE环境下返回WEB-INF/plugins/classes目录的实际路径，独立运行时返回class的根目录
	 * @author Darkness
	 * @date 2012-8-5 下午2:47:51 
	 * @version V1.0
	 */
	public static String getClassesPath() {
		if (classesPath == null) {
			URL url = Config.class.getClassLoader().getResource("rapidark.license");
			if (url == null) {
				System.err.println("Config.getClassesPath() failed!");
				return "";
			}
			try {
				String path = URLDecoder.decode(url.getPath(), Config.getFileEncode());
				if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
					if (path.startsWith("/")) {
						path = path.substring(1);
					}
				}
				if (path.startsWith("file:/")) {
					path = path.substring(6);
				} else if (path.startsWith("jar:file:/")) {
					path = path.substring(10);
				}
				if (path.indexOf(".jar!") > 0) {
					path = path.substring(0, path.indexOf(".jar!"));
				}
				path = path.replace('\\', '/');
				path = path.substring(0, path.lastIndexOf("/") + 1);
				if (path.indexOf("WEB-INF") >= 0) {
					path = path.substring(0, path.lastIndexOf("WEB-INF") + 7) + "/classes/";
				}
				if (System.getProperty("os.name").toLowerCase().indexOf("windows") < 0) {
					if (!path.startsWith("/")) {
						path = "/" + path;
					}
				}
				classesPath = path;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return classesPath;
	}

	/**
	 * 返回插件文件所在目录
	 * @method getPluginPath
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午2:47:51 
	 * @version V1.0
	 */
	public static String getPluginPath() {
		File f = new File(getClassesPath());
		String path = f.getParentFile().getAbsolutePath() + "/";
		return path;
	}

	/**
	 * 获取当前WEB应用下返回应用的实际路径
	 * @method getContextRealPath
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getContextRealPath() {
		if (configMap != null) {
			String str = configMap.get("App.ContextRealPath");
			if (str != null) {
				return str;
			}
		}
		String path = getClassesPath();
		int index = path.indexOf("WEB-INF");
		if (index > 0) {
			path = path.substring(0, index);
		}
		return path;
	}

	/**
	 * 返回应用路径，返回值以/结束。
	 * 考虑到同一个应用在内外网有不同的路径的情况，该处变量在每一次进入Filter后都会重新设置<br>
	 * @method getContextPath
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getContextPath() {
		if (isComplexDepolyMode) {
			String path = (String) Account.getValue("App.ContextPath");
			if (StringUtil.isEmpty(path)) {
				path = Config.getValue("App.ContextPath");
			}
			return path;
		} else {
			return Config.getValue("App.ContextPath");
		}
	}

	private static void initProduct() {
		if (appCode == null) {
			if (configMap.get("App.Code") != null) {
				appCode = configMap.get("App.Code");
				appName = configMap.get("App.Name");
			}
			if (appCode == null) {
				appCode = LangUtil.get("@{Product.Code}");
				appName = LangUtil.get("@{Product.Name}");
			}
			if (appCode == null) {
				appCode = "ARK";
				appName = "Ark Common Framework";
			}
		}
	}

	/**
	 * 获取系统log日志级别
	 * @method getLogLevel
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getLogLevel() {
		return Config.getValue("App.LogLevel");
	}
	
	/**
	 * 获取系统产品代码
	 * @method getAppCode
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getAppCode() {
		initProduct();
		return appCode;
	}

	/**
	 * 获取系统产品名称
	 * @method getAppName
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getAppName() {
		initProduct();
		return appName;
	}

	/**
	 * 是否是调试模式，调试模式将会自动复原Session
	 * @method isDebugMode
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isDebugMode() {
		if (isDebugMode == null) {
			isDebugMode = "true".equalsIgnoreCase(Config.getValue("App.DebugMode"));
		}
		return isDebugMode;
	}
	
	/**
	 * @return 中间件容器信息
	 * @method getContainerInfo
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getContainerInfo() {
		return Config.getValue("System.ContainerInfo");
	}

	/**
	 * @return 中间件容器的版本
	 * @method getContainerVersion
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getContainerVersion() {// NO_UCD
		String str = Config.getValue("System.ContainerInfo");
		if (str.indexOf("/") > 0) {
			return str.substring(str.lastIndexOf("/") + 1);
		}
		return "0";
	}

	/**
	 * 文本文件默认分隔符
	 * @method getLineSeparator
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getLineSeparator() {// NO_UCD
		return Config.getValue("System.LineSeparator");
	}

	/**
	 * 获取操作系统文件分隔符
	 * @method getFileSeparator
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getFileSeparator() {// NO_UCD
		return Config.getValue("System.FileSeparator");
	}

	/**
	 * 操作系统的默认文件编码
	 * @method getFileEncode
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getFileEncode() {
		return System.getProperty("file.encoding");
	}

	/**
	 * @return 己登录的后台用户数
	 */
	public static int getLoginUserCount() {
		int count = 0;
		for (HttpSession session : SessionListener.getMap().values()) {
			UserData ud = SessionListener.getUserDataFromSession(session);
			if (ud != null && ud.isLogin()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @return 己登录的会员数
	 */
	public static int getLoginMemberCount() {
		int count = 0;
		for (HttpSession session : SessionListener.getMap().values()) {
			UserData ud = SessionListener.getUserDataFromSession(session);
			if (ud != null && ud.getMemberData() != null && ud.getMemberData().isLogin) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 中间件是否是Tomcat
	 * @method isTomcat
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isTomcat() {
		if (StringUtil.isEmpty(Config.getContainerInfo())) {
			getJBossInfo();
		}
		return Config.getContainerInfo().toLowerCase().indexOf("tomcat") >= 0;
	}

	/**
	 * JBoss需要特别处理 JBoss调用ServletContext.getServerInfo()时会返回Apache Tomcat
	 * 5.x之类的， 且MainFilter会后面Config执行，需要特别处理
	 */
	public static void getJBossInfo() {
		String jboss = System.getProperty("jboss.home.dir");
		if (StringUtil.isNotEmpty(jboss)) {
			try {
				Class<?> c = Class.forName("org.jboss.Version");
				Method m = c.getMethod("getInstance", (Class[]) null);
				Object o = m.invoke(null, (Object[]) null);
				m = c.getMethod("getMajor", (Class[]) null);
				Object major = m.invoke(o, (Object[]) null);
				m = c.getMethod("getMinor", (Class[]) null);
				Object minor = m.invoke(o, (Object[]) null);
				m = c.getMethod("getRevision", (Class[]) null);
				Object revision = m.invoke(o, (Object[]) null);
				m = c.getMethod("getTag", (Class[]) null);
				Object tag = m.invoke(o, (Object[]) null);
				Config.configMap.put("System.ContainerInfo", "JBoss/" + major + "." + minor + "." + revision + "." + tag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 中间件是否是JBoss
	 * @method isJboss
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isJboss() {
		if (StringUtil.isEmpty(Config.getContainerInfo())) {
			getJBossInfo();
		}
		return Config.getContainerInfo().toLowerCase().indexOf("jboss") >= 0;
	}

	/**
	 * 中间件是否是WebLogic
	 * @method isWeblogic
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isWeblogic() {
		return Config.getContainerInfo().toLowerCase().indexOf("weblogic") >= 0;
	}

	/**
	 * 中间件是否是WebSphere
	 * @method isWebSphere
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isWebSphere() {
		return Config.getContainerInfo().toLowerCase().indexOf("websphere") >= 0;
	}

	/**
	 * 系统是否是复杂部署模式，该模式下一个应用可能因为对外暴露的地址不一样而有多个ContextPath
	 * @method isComplexDepolyMode
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isComplexDepolyMode() {
		return isComplexDepolyMode;
	}

	/**
	 * 获取后台用户登陆页面（相对于应用路径的地址）
	 * @method getLoginPage
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getLoginPage() {
		String str = configMap.get("App.LoginPage");
		if (StringUtil.isNotEmpty(str)) {// 可能是没有配置文件
			return str;
		}
		return "login.zhtml";
	}

	/**
	 *  获取应用全局字符集
	 *  @method getGlobalCharset
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getGlobalCharset() {
		if (globalCharset == null) {
			init();
			configLoader.load();
		}
		if (globalCharset == null) {// 不存在charset.config
			globalCharset = "UTF-8";
		}
		return globalCharset;
	}

	/**
	 * 当前运行环境是否是一个插件上下文。如果不是插件上下文，则插件和扩展不会加载。
	 * @method isPluginContext
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isPluginContext() {
		return isPluginContext;
	}

	/**
	 * 设置当前运行环境是否是一个插件上下文。如果不是插件上下文，则插件和扩展不会加载。
	 * @method setPluginContext
	 * @param {boolean} isPluginContext 是否为插件方式
	 * @private 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static void setPluginContext(boolean isPluginContext) {
		Config.isPluginContext = isPluginContext;
	}

	/**
	 * 设置是否是前置部署
	 */
	public static void setFrontDeploy(boolean flag) {// NO_UCD
		isFrontDeploy = flag;
	}

	public static boolean isInstalled() {
		return isInstalled;
	}

	public static boolean isAllowLogin() {
		return isAllowLogin;
	}

	public static void setAllowLogin(boolean isAllowLogin) {
		Config.isAllowLogin = isAllowLogin;
	}

	public static void setInstalled(boolean isInstalled) {
		Config.isInstalled = isInstalled;
	}

	public static int getOnlineUserCount() {
		return SessionListener.getMap().size();
	}

	public static int getServletMajorVersion() {
		return servletMajorVersion;
	}

	public static int getServletMinorVersion() {
		return servletMinorVersion;
	}

	public static Boolean getIsDebugMode() {
		return isDebugMode;
	}

	public static void setServletMajorVersion(int servletMajorVersion) {
		Config.servletMajorVersion = servletMajorVersion;
	}

	public static void setServletMinorVersion(int servletMinorVersion) {
		Config.servletMinorVersion = servletMinorVersion;
	}

	public static void put(String key, String value) {
		configMap.put(key, value);
	}
	
	/**
	 * 获取系统产品主版本号
	 * @method getMainVersion
	 * @return {float} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
//	public static float getMainVersion() {
//		initProduct();
//		return MainVersion;
//	}

	/**
	 * 获取系统产品次版本号
	 * @method getMinorVersion
	 * @return {float} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
//	public static float getMinorVersion() {
//		initProduct();
//		return MinorVersion;
//	}

	

	/**
	 * 获取系统Java版本号
	 * @method getJavaVersion
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getJavaVersion() {
		return com.rapidark.framework.Config.getValue("System.JavaVersion");
	}

	/**
	 * 获取系统Java提供商
	 * @method getJavaVendor
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getJavaVendor() {
		return com.rapidark.framework.Config.getValue("System.JavaVendor");
	}

	/**
	 * 获取系统Java Home路径
	 * @method getJavaHome
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getJavaHome() {
		return getValue("System.JavaHome");
	}



	/**
	 * 获取操作系统名称
	 * @method getOSName
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getOSName() {
		return getValue("System.OSName");
	}

	/**
	 * 获取操作系统补丁级别
	 * @method getOSPatchLevel
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getOSPatchLevel() {
		return getValue("System.OSPatchLevel");
	}

	/**
	 * 获取操作系统Arch
	 * @method getOSArch
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getOSArch() {
		return getValue("System.OSArch");
	}

	/**
	 * 获取操作系统版本号
	 * @method getOSVersion
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getOSVersion() {
		return getValue("System.OSVersion");
	}

	/**
	 * 获取操作系统语言
	 * @method getOSUserLanguage
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getOSUserLanguage() {
		return getValue("System.OSUserLanguage");
	}

	/**
	 * 获取操作系统用户名
	 * @method getOSUserName
	 * @return {String} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static String getOSUserName() {
		return getValue("System.OSUserName");
	}

	/**
	 * 判断当前系统使用数据库是否是db2
	 * @method isDB2
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isDB2() {
		return ConnectionPoolManager.getDBConnConfig().isDB2();
	}

	/**
	 * 判断当前系统使用数据库是否是SQLServer
	 * @method isSQLServer
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isSQLServer() {
		return ConnectionPoolManager.getDBConnConfig().isSQLServer();
	}

	/**
	 * 判断当前系统使用数据库是否是Sybase
	 * @method isSybase
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isSybase() {
		return ConnectionPoolManager.getDBConnConfig().isSybase();
	}

	/**
	 * 系统log级别是否为debug
	 * @method isDebugLoglevel
	 * @return {boolean} 
	 * @author Darkness
	 * @date 2012-8-5 下午4:57:09 
	 * @version V1.0
	 */
	public static boolean isDebugLoglevel() {
		return "Debug".equalsIgnoreCase(getLogLevel());
	}

//	/**
//  * 系统在线用户数
//  * @property OnlineUserCount
//  * @type {int}
//  */
//	public static int OnlineUserCount = 0;
//
//	/**
//  * 系统登录用户数
//  * @property LoginUserCount
//  * @type {int}
//  */
//	public static int LoginUserCount = 0;
//
//	/**
//  * 系统是否已安装
//  * @property isInstalled
//  * @type {Boolean}
//  */
//	public static boolean isInstalled = false;
//
//	/**
//  * 系统是否允许登录
//  * @property isAllowLogin
//  * @type {Boolean}
//  */
//	public static boolean isAllowLogin = true;
//
//	public static boolean isPluginContext = false;
//
//	private static String AppCode = null;
//
//	private static String AppName = null;
//
//	private static float MainVersion = 1.0F;
//
//	private static float MinorVersion = 0.0F;
//
//	public static boolean ComplexDepolyMode = false;
//
//	/**
//  * 系统servlet主版本号
//  * @property ServletMajorVersion
//  * @type {int}
//  */
	public static int ServletMajorVersion;
//	
//	/**
//  * 系统servlet次版本号
//  * @property ServletMinorVersion
//  * @type {int}
//  */
	public static int ServletMinorVersion;
//	protected static String GlobalCharset;
//	private static String ClassPath;

	public static List<XMLElement> getElements(String path) {
		init();
		return configLoader.getElements(path);
	}

	public static void reloadXmlConfig() {
		init();
		configLoader.reload();
	}
}
