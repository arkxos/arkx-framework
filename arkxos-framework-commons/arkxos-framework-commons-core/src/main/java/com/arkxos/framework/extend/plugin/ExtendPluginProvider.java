package com.arkxos.framework.extend.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.arkxos.framework.Config;
import com.arkxos.framework.commons.collection.CacheMapx;
import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.PropertiesUtil;
import com.arkxos.framework.cosyui.web.mvc.SessionListener;
import com.arkxos.framework.extend.ExtendActionConfig;
import com.arkxos.framework.extend.ExtendItemConfig;
import com.arkxos.framework.extend.ExtendManager;
import com.arkxos.framework.extend.ExtendPointConfig;
import com.arkxos.framework.extend.ExtendServiceConfig;
import com.arkxos.framework.extend.action.AfterAllPluginStartedAction;
import com.arkxos.framework.schedule.CronManager;

/**   
 * @class org.ark.framework.extend.plugin.ExtendPluginProvider
 * @private
 * @author Darkness
 * @date 2012-12-5 下午04:48:34 
 * @version V1.0   
 */
public class ExtendPluginProvider {
	private static ExtendPluginProvider instance = new ExtendPluginProvider();
	
	public static ExtendPluginProvider getInstance() {
		return instance;
	}
	
	private Map<String, ArrayList<ExtendActionConfig>> extendActionMap;
	private Map<String, ArrayList<ExtendItemConfig>> extendItemMap;
	private Map<String, ExtendPointConfig> extendPointMap;
	private Map<String, ExtendServiceConfig> extendServiceMap;
	private Map<String, ExtendServiceConfig> extendServiceClassMap;
	
	private ReentrantLock lock = new ReentrantLock();
	
	/**
	 * 启动扩展插件，加载插件配置文件，初始化相关扩展数据。
	 * @author Darkness
	 * @date 2012-8-6 下午10:05:55 
	 * @version V1.0
	 */
	public void start() {
		if (extendActionMap == null) {
			lock.lock();
			try {
				if (extendActionMap == null) {
					extendActionMap = new CacheMapx<>();
					extendItemMap = new CacheMapx<>();
					extendPointMap = new CacheMapx<>();
					extendServiceMap = new CacheMapx<>();
					extendServiceClassMap = new CacheMapx<>();

					// 先读取所有插件信息
					long t = System.currentTimeMillis();

					List<IPlugin> list = new ArrayList<>();

					List<PluginConfig> configList = PluginManager.getInstance().getAllPluginConfig();
					for (PluginConfig pc : configList) {
						if (!pc.isEnabled() || pc.isRunning()) {
							continue;
						}
						initPlugin(pc, list);
					}
					
					registerExtendPoints();
					
					registerExtendActions();
					
					registerExtendService();
					
					// 所有扩展信息读取完成后再逐个启动
					for (IPlugin plugin : list) {
						try {
							plugin.start();
						} catch (PluginException e) {
							e.printStackTrace();
						}
					}
					LogUtil.info("All plugins started,cost " + (System.currentTimeMillis() - t) + " ms");
				}
			} finally {
				lock.unlock();
				ExtendManager.invoke(AfterAllPluginStartedAction.ExtendPointID);
			}
		}
	}
	/**
	 * 初始插件中的配置信息
	 * @param pc 插件配置
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午10:57:25 
	 * @version V1.0
	 */

	private void initPlugin(PluginConfig pc, List<IPlugin> list) {
		if (pc == null || pc.isRunning()) {
			return;
		}
		if (ObjectUtil.notEmpty(pc.getClassName())) {
			try {
				LogUtil.debug("Loading plugin:" + pc.getID());
				pc.setRunning(true);// 需要先设置，以免无限递归
				pc.setEnabled(true);
				for (String id : pc.getRequiredPlugins().keySet()) {
					initPlugin(PluginManager.getInstance().getPluginConfig(id), list);
				}
				
				Class<?> c = Class.forName(pc.getClassName());
				if (!IPlugin.class.isAssignableFrom(c)) {
					LogUtil.error("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
					return;
				}
				readExtendInfo(pc);
				IPlugin plugin = (IPlugin) c.newInstance();
				list.add(plugin);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void readExtendInfo(PluginConfig pc) {
		// 加入扩展点
		extendPointMap.putAll(pc.getExtendPoints());

		// 加入扩展服务
		for (ExtendServiceConfig es : pc.getExtendServices().values()) {
			extendServiceMap.put(es.getID(), es);
			extendServiceClassMap.put(es.getClassName(), es);
		}

		// 加入扩展行为
		Collection<ExtendActionConfig> actions = pc.getExtendActions().values();
		for (ExtendActionConfig action : actions) {
			// LogUtil.debug("\tLoading extendAction:" + action.getID());
			if (!extendPointMap.containsKey(action.getExtendPointID())) {
				LogUtil.error("ExtendAction " + action.getID() + "'s ExtendPoint not found");
				continue;
			}
			ArrayList<ExtendActionConfig> list = extendActionMap.get(action.getExtendPointID());
			if (list == null) {
				list = new ArrayList<>();
				extendActionMap.put(action.getExtendPointID(), list);
			}
			list.add(action);
		}

		// 加入扩展项
		Collection<ExtendItemConfig> items = pc.getExtendItems().values();
		for (ExtendItemConfig item : items) {
			// LogUtil.debug("\tLoading extendItem:" + item.getID());
			if (!extendServiceMap.containsKey(item.getExtendServiceID())) {
				LogUtil.error("ExtendItem " + item.getID() + "'s ExtendService not found");
				continue;
			}

			ArrayList<ExtendItemConfig> list = extendItemMap.get(item.getExtendServiceID());
			if (list == null) {
				list = new ArrayList<>();
				extendItemMap.put(item.getExtendServiceID(), list);
			}
			list.add(item);
		}
	}
	
	/**
	 * 启用插件
	 */
	public void startPlugin(PluginConfig pc) throws PluginException {
		if (pc == null || pc.isRunning()) {
			return;
		}
		if (ObjectUtil.notEmpty(pc.getClassName())) {
			try {
				LogUtil.debug("Starting plugin:" + pc.getID());
				pc.setRunning(true);// 需要先设置，以免无限递归
				pc.setEnabled(true);
				for (String id : pc.getRequiredPlugins().keySet()) {
					startPlugin(PluginManager.getInstance().getPluginConfig(id));
				}
				Class<?> c = Class.forName(pc.getClassName());
				if (!IPlugin.class.isAssignableFrom(c)) {
					LogUtil.error("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
					return;
				}
				readExtendInfo(pc);
				IPlugin plugin = (IPlugin) c.newInstance();
				plugin.start();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查找被指定插件依赖的插件列表
	 * 
	 * @param pc  插件
	 * @return 依赖该插件的插件列表
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午11:01:49
	 * @version V1.0
	 */
	public List<PluginConfig> getRequiredPlugins(PluginConfig pc) throws PluginException {
		ArrayList<PluginConfig> list = new ArrayList<>();
		for (PluginConfig pc2 : PluginManager.getInstance().getAllPluginConfig()) {
			if (!pc2.getID().equals(pc.getID()) && pc2.getRequiredPlugins().containsKey(pc.getID())) {
				list.add(pc2);
			}
		}
		return list;
	}

	/**
	 * 停止插件
	 * @param pc 需要停止的插件配置
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午11:02:45 
	 * @version V1.0
	 */
	public void stopPlugin(PluginConfig pc) throws PluginException {
		if (!pc.isEnabled() || !pc.isRunning()) {
			return;
		}
		if (ObjectUtil.notEmpty(pc.getClassName())) {
			try {
				Class<?> c = Class.forName(pc.getClassName());
				if (!IPlugin.class.isAssignableFrom(c)) {
					throw new PluginException("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
				}
				IPlugin plugin = (IPlugin) c.newInstance();
				plugin.stop();

				for (PluginConfig pc2 : getRequiredPlugins(pc)) {
					stopPlugin(pc2);
				}
				pc.setRunning(false);
				pc.setEnabled(false);

				for (ExtendActionConfig ea : pc.getExtendActions().values()) {
					extendActionMap.get(ea.getExtendPointID()).remove(ea);
				}

				// 去掉扩展点和扩展行为
				for (String id : pc.getExtendPoints().keySet()) {
					extendPointMap.remove(id);
				}
				// 移除相应的扩展项(必须在移除扩展服务之前，因为本插件可以自己注册自己的扩展服务的扩展项)
				for (ExtendItemConfig ei : pc.getExtendItems().values()) {
					ExtendServiceConfig es = extendServiceMap.get(ei.getExtendServiceID());
					if (es != null) {
						es.getInstance().remove(ei.getInstance().getExtendItemID());
					}
					extendItemMap.get(ei.getExtendServiceID()).remove(ei);
				}
				// 去掉扩展服务
				for (ExtendServiceConfig es : pc.getExtendServices().values()) {
					extendServiceMap.remove(es.getID());
					extendServiceClassMap.remove(es.getID());
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 停止掉所有的插件
	 */
	public void destory() {
		for (PluginConfig pc : PluginManager.getInstance().getAllPluginConfig()) {
			try {
				Class<?> c = Class.forName(pc.getClassName());
				if (!IPlugin.class.isAssignableFrom(c)) {
					throw new PluginException("Plugin class '" + pc.getClassName() + "' isn't inherit from IPlugin");
				}
				IPlugin plugin = (IPlugin) c.newInstance();
				plugin.destory();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (extendServiceMap != null) {
			for (ExtendServiceConfig es : extendServiceMap.values()) {
				es.destory();
			}
			extendServiceMap.clear();
			extendServiceMap = null;
		}
		if (extendActionMap != null) {
			extendActionMap.clear();
			extendActionMap = null;
		}
		if (extendItemMap != null) {
			extendItemMap.clear();
			extendItemMap = null;
		}
		if (extendPointMap != null) {
			extendPointMap.clear();
			extendPointMap = null;
		}
		if (extendServiceClassMap != null) {
			extendServiceClassMap.clear();
			extendServiceClassMap = null;
		}
		PluginManager.getInstance().destory();
	}
	
	/**
	 * 指定的扩展点下是否有扩展行为。
	 */
	public boolean hasAction(String targetPoint) {
		start();
		return extendActionMap.get(targetPoint) != null;
	}

	/**
	 * 查找扩展了指定扩展点的扩展行为列表
	 */
	public ArrayList<ExtendActionConfig> findActionsByPointID(String extendPointID) {
		start();
		return extendActionMap.get(extendPointID);
	}

	/**
	 * 查找注册到指定扩展服务的扩展项列表
	 */
	public ArrayList<ExtendItemConfig> findItemsByServiceID(String extendServiceID) {
		start();
		return extendItemMap.get(extendServiceID);
	}

	/**
	 * 根据扩展点类名查找扩展点描述
	 */
	public ExtendPointConfig findExtendPoint(String extendPointID) {
		start();
		return extendPointMap.get(extendPointID);
	}

	/**
	 * 根据扩展服务ID查找扩展服务描述
	 */
	public ExtendServiceConfig findExtendService(String extendServiceID) {// NO_UCD
		start();
		return extendServiceMap.get(extendServiceID);
	}

	/**
	 * 根据扩展服务类名查找扩展服务描述
	 * 
	 * @param className 扩展服务类名
	 * @return 扩展服务描述类
	 */
	public ExtendServiceConfig findExtendServiceByClass(String className) {
		start();
		return extendServiceClassMap.get(className);
	}

	/**
	 * 启用插件
	 */
	public void enablePlugin(String pluginID) throws PluginException {
		startPlugin(PluginManager.getInstance().getPluginConfig(pluginID));
		setStatusValue(pluginID, "true");
//		MenuManager.reloadMenus();
	}

	/**
	 * 停用插件
	 */
	public void disablePlugin(String pluginID) throws PluginException {
		stopPlugin(PluginManager.getInstance().getPluginConfig(pluginID));
//		MenuManager.reloadMenus();
		setStatusValue(pluginID, "false");
	}

	/**
	 * 启用菜单
	 */
	public void enableMenu(String menuID) {
		setStatusValue("MENU." + menuID, "true");
	}

	/**
	 * 停用菜单
	 */
	public void disableMenu(String menuID) {
		setStatusValue("MENU." + menuID, "false");
	}

	/**
	 * 插件是否被启用
	 */
	public boolean isPluginEnable(String pluginID) {// NO_UCD
		return !"false".equals(PluginManager.getInstance().getStatusMap().get(pluginID));
	}

	/**
	 * 菜单是否被启用
	 */
	public boolean isMenuEnable(String menuID) {
		return !"false".equals(PluginManager.getInstance().getStatusMap().get("MENU." + menuID));
	}

	/**
	 * 将状态值写入文件
	 */
	public void setStatusValue(String key, String value) {
		File f = new File(Config.getPluginPath() + "classes/plugins/status.config");
		PluginManager.getInstance().getStatusMap().put(key, value);
		PropertiesUtil.write(f, PluginManager.getInstance().getStatusMap());
		if (FileUtil.exists(new File(Config.getContextRealPath()).getParentFile().getAbsolutePath() + "/JAVA")) {
			File ff = new File(new File(Config.getContextRealPath()).getParentFile().getAbsolutePath() + "/JAVA/plugins/status.config");
			PropertiesUtil.write(ff, PluginManager.getInstance().getStatusMap());
		}

	}

	/**
	 * 重新启动插件运行环境。<br>
	 * 本方法将先中止所有会话和定时任务。<br>
	 * 一般在系统安装、插件安装卸载时调用。
	 */
	public void restart() {// NO_UCD
		SessionListener.forceExit();// 现有会话强制退出
		Config.setAllowLogin(false);// 暂时不允许登录
		CronManager.getInstance().destory();// 定时任务强制退出

		extendActionMap = null;
		start();// 重新读入掉插件注册信息
		Config.setAllowLogin(true);
	}
	
	private void registerExtendPoints() {
		
		if(extendPointMap == null) {
			return;
		}
		
		for (String extendPointId : extendPointMap.keySet()) {
			ExtendManager.registerExtendPoint(extendPointId);
		}
		
	}
	
	/**
	 * 注册扩展服务
	 * 
	 * @author Darkness
	 * @date 2012-12-5 下午05:08:54 
	 * @version V1.0
	 */
	private  void registerExtendService() {
		
		if(extendServiceClassMap == null) {
			return;
		}
		
		for (String extendServiceClassName : extendServiceClassMap.keySet()) {
			ExtendManager.registerExtendService(extendServiceClassName, extendServiceClassMap.get(extendServiceClassName).getInstance());
		}
	}

	/**
	 * 注册扩展行为
	 * 
	 * @author Darkness
	 * @date 2012-12-5 下午05:03:57 
	 * @version V1.0
	 */
	private  void registerExtendActions() {
		
		if(extendPointMap == null) {
			return;
		}
		
		for (String extendPointId : extendPointMap.keySet()) {
			ArrayList<ExtendActionConfig> actions = findActionsByPointID(extendPointId);
			if (actions == null) {
				continue;
			}
			for (ExtendActionConfig eac : actions) {
				try {
					ExtendManager.registerExtendAction(extendPointId, eac.getInstance());
				} catch (Exception e) {
					e.printStackTrace();
				//	throw new ExtendException(e.getMessage());
				}
			}
		}
	}
}
