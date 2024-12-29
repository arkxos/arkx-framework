package com.arkxos.framework.extend.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.arkxos.framework.annotation.PluginAnnotationScannerParser;
import com.arkxos.framework.commons.collection.ConcurrentMapx;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.collection.ReadOnlyList;
import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.commons.util.PropertiesUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.rapidark.framework.Config;

import lombok.extern.slf4j.Slf4j;

/**
 * @class org.ark.framework.extend.plugin.PluginManager
 * 插件管理器
 * @private
 * @author Darkness
 * @date 2012-8-5 下午7:43:55 
 * @version V1.0
 */
@Slf4j
public class PluginManager {
	
	private static PluginManager instance = new PluginManager();
	
	public static PluginManager getInstance() {
		return instance;
	}
	
	private List<PluginConfig> configList = null;
	private ConcurrentMapx<String, String> statusMap = new ConcurrentMapx<>();
	private ReentrantLock lock = new ReentrantLock();
	
	/**
	 * 插件初始化
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:44:33 
	 * @version V1.0
	 */
	public void init() {
		if (configList == null || configList.isEmpty()) {
			lock.lock();
			try {
				if (configList == null || configList.isEmpty()) {
					load();
				}
			} finally {
				lock.unlock();
			}
		}
	}
	
	/**
	 * 加载web-inf下配置的所有插件配置文件
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:44:48 
	 * @version V1.0
	 */
	private void load() {
		pluginFolders.clear();
		String[] defaultPluginFolders = getPluginProvider().getPluginFolders();
		for (String defaultPluginFolder : defaultPluginFolders) {
			pluginFolders.add(defaultPluginFolder);
		}
		
		List<String> pluginIds = PluginAnnotationScannerParser.getPluginIds();
		if (pluginIds.isEmpty()) {
			pluginIds = Arrays.asList("com.rapidark.framework");
		}
		configList = loadAllConfig(pluginIds);
	}
	
	// 插件目录
	public static List<String> pluginFolders = new ArrayList<>();

	/**
	 * 添加插件目录
	 * 
	 * @author Darkness
	 * @date 2012-11-23 下午04:57:51
	 * @version V1.0
	 */
	public static void addPluginFolder(String pluginFolder) {
		pluginFolders.add(pluginFolder);
	}

	private static int PLUGIN_PROVIDER_TYPE = IPluginProvider.TEST;
	
	/**
	 * web plugin mode
	 * 
	 * @author Darkness
	 * @date 2012-11-23 下午02:57:42 
	 * @version V1.0
	 */
	public static void initWebPlugin() {
		PLUGIN_PROVIDER_TYPE = IPluginProvider.WEB;
	}

	/**
	 * test plugin mode
	 * 
	 * @author Darkness
	 * @date 2012-11-23 下午04:15:33 
	 * @version V1.0
	 */
	public static void initTestPlugin() {
		PLUGIN_PROVIDER_TYPE = IPluginProvider.TEST;
	}
	
	public static IPluginProvider getPluginProvider() {
		
		if(PLUGIN_PROVIDER_TYPE == IPluginProvider.WEB)
			return new WebPluginProvider();
		
		if(PLUGIN_PROVIDER_TYPE == IPluginProvider.TEST)
			return new JavaTestPluginProvider();
		
		return new JavaPluginProvider();
	}

	public void destory() {
		statusMap.clear();
		configList = null;
//		statusMap = null;
	}

	public Map<String, String> getStatusMap() {
		return statusMap;
	}

	/**
	 * 读取指定应用下的所有插件配置文件,参数path应该是一个UI/WEB-INF目录
	 */
	/**
	 * 加载插件配置
	 * 
	 * 加载规则：
	 * 	1、加载lib下的包含“-plugin-*.jar”的jar，读取jar中的“.plugin”文件
	 * 	2、加载classes/plugins下的“.plugin”文件
	 * 
	 * @param path web-inf目录
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:46:37 
	 * @version V1.0
	 */
	private List<PluginConfig> loadAllConfig(List<String> pluginIds) {
		ArrayList<PluginConfig> list = new ArrayList<>();
		Mapx<String, String> map = new Mapx<>();
		statusMap.clear();

		// 读取插件配置
		for (String pluginId : pluginIds) {
			File statusFile = new File(Config.getClassesPath() + File.pathSeparator + "plugin" + File.separator + pluginId + "status.config");
			if (statusFile.exists()) {
				statusMap.putAll(PropertiesUtil.read(statusFile));
			}
		}

//		if (!Config.isPluginContext()) {
//			return;// 如果不是插件环境，则只返回Framework本身的配置信息
//		}

		for (String pluginId : pluginIds) {
			loadPluginFile(pluginId, list, map);
		}
		
		for (PluginConfig pc : list) {
			if ("false".equals(statusMap.get(pc.getID()))) {
				pc.setEnabled(false);// 如果配置中停用，则置为false
			}
		}
		computeRela(list);// 计算插件依赖关系

		ArrayList<PluginConfig> result = new ArrayList<>();
		ArrayList<PluginConfig> tmp = new ArrayList<>();
		tmp.addAll(list);
		for (PluginConfig pc : tmp) {
			sort(result, list, pc);
		}
		return new ReadOnlyList<>(result);
	}
	
	private static List<IPluginParser> pluginParsers = new ArrayList<>();

	static {
		pluginParsers.add(new JarPluginParser());
		pluginParsers.add(new FilePluginParser());
	}

	private void loadPluginFile(String pluginId, ArrayList<PluginConfig> list, Mapx<String, String> map) {

		InputStream inputStream = this.getClass().getResourceAsStream("/plugins/" + pluginId + ".plugin");
		
		if (inputStream == null) {
			log.debug("插件：" + pluginId + "不存在！");
			return;
		}
		
//		File[] fs = new File(path).listFiles();
//		for (File f : fs) {
			
//			for (IPluginParser pluginParser : pluginParsers) {
//				
//				if(pluginParser.validate(f)) {
		
		PluginConfig pc = new PluginConfig();
		try {
			pc.parse(FileUtil.readText(inputStream, "UTF-8"));
//			pc.setUpdateSite(f.getAbsolutePath());
//			pc.setPackageFile(f.getAbsolutePath());
		} catch (PluginException e) {
			e.printStackTrace();
		}
		
//		PluginConfig[] pluginConfigs = pluginParser.parse(f);
					
//					for (PluginConfig pc : pluginConfigs) {
						if (!map.containsKey(pc.getID())) {
							list.add(pc);
						} else {
							LogUtil.warn("PluginConfig is duplication:" + map.get(pc.getID()) + " & " + pc.getUpdateSite());
						}
						map.put(pc.getID(), pc.getUpdateSite());
//					}
//				}
//			}
//		}
	}

	/**
	 * 逆序排列
	 * 
	 * @param result
	 * @param configList
	 * @param pc
	 */
	private void sort(List<PluginConfig> result,ArrayList<PluginConfig> configList, PluginConfig pc) {
//		if (getPluginConfig(result, pc.getID()) != null) {
//			return;
//		}
		configList.remove(pc);// 避免出现死循环
		for (String pluginID : pc.getRequiredPlugins().keySet()) {
			PluginConfig c = getPluginConfig(configList, pluginID);
			if (c == null) {
				continue;
			}
			sort(result, configList, c);
		}
		if (getPluginConfig(result, pc.getID()) == null) {
			result.add(pc);
		}
	}

	private void computeRela(ArrayList<PluginConfig> configList) {
		for (PluginConfig pc : configList) {
			if (!pc.isEnabled()) {
				continue;// 已经被停用，不需要计算
			}

			// 如果依赖的插件不存在，则置为false
			boolean requiredFlag = true;// 默认满足
			for (String pluginID : pc.getRequiredPlugins().keySet()) {
				PluginConfig c = getPluginConfig(configList, pluginID);
				if (c == null || !c.isEnabled()) {
					if (c == null) {
						LogUtil.error("Plugin " + pluginID + " needed by " + pc.getID() + " is not found!");
					}
					requiredFlag = false;
					break;
				}
				// 目标插件的版本是否符合要求
				String v = c.getVersion();
				String need = pc.getRequiredPlugins().get(pluginID);
				if (!isVersionCompatible(need, v)) {
					LogUtil.error("Plugin " + pluginID + "'s version is " + v + ", but " + need + " is needed by " + pc.getID() + "!");
				}
			}
			if (!requiredFlag) {
				setDisable(pc);
				continue;// 接着继续下一个
			}

			// 如果依赖的扩展点不存在，则置为false
			requiredFlag = true;
			for (String extendPointID : pc.getRequiredExtendPoints().keySet()) {
				boolean flag = false;
				for (PluginConfig c : configList) {
					if (c.getExtendPoints().containsKey(extendPointID)) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					LogUtil.error("ExtendPoint " + extendPointID + " needed by " + pc.getID() + " is not found!");
					requiredFlag = false;
					break;
				}
			}
			if (!requiredFlag) {
				setDisable(pc);
				continue;// 接着继续下一个
			}
		}
	}

	/**
	 * 判断版本是否兼容
	 */
	private boolean isVersionCompatible(String need, String version) {
		if (need.indexOf("-") > 0) {
			String[] arr = StringUtil.splitEx(need, "-");
			if (arr.length != 2) {
				return false;
			}
			String start = arr[0];
			String end = arr[1];
			if (start.endsWith(".x")) {
				start = start.substring(0, start.length() - 2);
			}
			if (end.endsWith(".x")) {
				end = end.substring(0, end.length() - 2);
			}
			double s = Double.parseDouble(start);
			double e = Double.parseDouble(end);
			double v = Double.parseDouble(version);
			return s <= v && e >= v;
		} else {
			if (need.endsWith(".x")) {
				return version.startsWith(need.substring(0, need.length() - 1));
			} else {
				return getVersion(version) >= getVersion(need);
			}
		}
	}

	private double getVersion(String ver) {
		int i1 = ver.indexOf('.');
		if (i1 > 0) {
			int i2 = ver.indexOf('.', i1 + 1);
			if (i2 > 0) {
				ver = ver.substring(0, i2);
			}
		}
		try {
			return Double.parseDouble(ver);
		} catch (Exception e) {
			LogUtil.info("Invalid version number:" + ver);
			return 0;
		}
	}

	private void setDisable(PluginConfig pc) {
		if (!pc.isEnabled()) {
			return;// 不需要再计算
		}
		pc.setEnabled(true);
		for (PluginConfig c : configList) {
			if (c.getID().equals(pc.getID())) {
				continue;
			}
			if (c.getRequiredPlugins().containsKey(pc.getID())) {
				setDisable(c);
				continue;
			}
			for (String extendPointID : c.getRequiredExtendPoints().keySet()) {
				if (pc.getExtendPoints().containsKey(extendPointID)) {
					setDisable(c);
					break;
				}
			}
		}
	}

	public PluginConfig getPluginConfig(List<PluginConfig> list, String pluginID) {
//		init();
		for (PluginConfig c : list) {
			if (c.getID().equals(pluginID)) {
				return c;
			}
		}
		return null;
	}

	public PluginConfig getPluginConfig(String pluginID) {
		init();
		return getPluginConfig(configList, pluginID);
	}

	public List<PluginConfig> getAllPluginConfig() {
		init();
		return configList;
	}
	
}