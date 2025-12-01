package io.arkx.framework.commons.plugin;

import ro.fortsoft.pf4j.DefaultPluginManager;
import ro.fortsoft.pf4j.PluginManager;

import java.util.List;

public class PluginContainer {

	private static PluginContainer instance;
	private static Object syncObject = new Object();
	
	public static void init() {
		if(instance == null) {
			synchronized (syncObject) {
				if(instance == null) {
					// create the plugin manager
			        final PluginManager pluginManager = new DefaultPluginManager();

			        // load the plugins
			        pluginManager.loadPlugins();

			        // enable a disabled plugin
//			        pluginManager.enablePlugin("welcome-plugin");

			        // start (active/resolved) the plugins
			        pluginManager.startPlugins();
			        
			        instance = new PluginContainer(pluginManager);
				}
			}
		}
	}
	
	public static <T> List<T> getExtensions(Class<T> type) {
		init();
		
		return instance.pluginManager.getExtensions(type);
	}
	
	PluginManager pluginManager;

	private PluginContainer(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
}
