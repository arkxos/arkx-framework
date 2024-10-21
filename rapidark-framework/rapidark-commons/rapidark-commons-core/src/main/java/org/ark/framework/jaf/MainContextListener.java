package org.ark.framework.jaf;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.ark.framework.schedule.CronManager;

import com.rapidark.framework.Config;
import com.rapidark.framework.extend.plugin.PluginManager;


/**
 * @class org.ark.framework.MainContextListener
 * 系统启动上下文监听器
 * 
 * @author Darkness
 * @date 2012-11-23 下午01:36:59 
 * @version V1.0
 */
public class MainContextListener implements ServletContextListener {
	
	private CronManager manager;

	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			if (this.manager != null)
				this.manager.destory();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void contextInitialized(ServletContextEvent event) {
		
		ServletContext sc = event.getServletContext();
		
		Config.setValue("System.ContainerInfo", sc.getServerInfo());
		
		Config.getJBossInfo();
		
		try {
			Config.loadConfig();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		Config.setPluginContext(true);
		
		PluginManager.initWebPlugin();
		
		this.manager = CronManager.getInstance();
		
//		ExtendManager.start();
	}
	
}