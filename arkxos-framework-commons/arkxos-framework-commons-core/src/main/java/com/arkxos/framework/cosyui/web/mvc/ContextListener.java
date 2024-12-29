package com.arkxos.framework.cosyui.web.mvc;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import com.arkxos.framework.cache.CacheManager;
import com.arkxos.framework.data.db.connection.ConnectionPoolManager;
import com.arkxos.framework.extend.plugin.ExtendPluginProvider;
import com.arkxos.framework.schedule.CronManager;
import com.rapidark.framework.Config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
import com.rapidark.preloader.PreClassLoader;
 * Servlet上下文监听器
 * 
 */
@WebListener
public class ContextListener implements ServletContextListener {
	private CronManager manager;

	/**
	 * 上下文销毁时清除掉某些全局对象
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if (manager != null) {
			manager.destory();
		}
		CacheManager.destory();
		ExtendPluginProvider.getInstance().destory();
		ConnectionPoolManager.destory();
		cleanJdbcDriverManager();
	}

	/**
	 * 清除所有的JDBC驱动管理器
	 */
	private void cleanJdbcDriverManager() {
		List<String> driverNames = new ArrayList<String>();
		HashSet<Driver> originalDrivers = new HashSet<Driver>();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			originalDrivers.add(drivers.nextElement());
		}
		drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
//			if (driver.getClass().getClassLoader() != PreClassLoader.getInstance()) {
//				continue;
//			}
			if (originalDrivers.contains(driver)) {
				driverNames.add(driver.getClass().getCanonicalName());
			}
			try {
				DriverManager.deregisterDriver(driver);
//				System.out.println("Unregister JDBC driver in ContextListener success:" + driver);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 上下文初始化时同时初始化一些全局对象
	 * 
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// 以下两行代码MainFilter中也有，有可能是MainFilter先初始化，也有可能是MainContextListener先初始化（考虑WebSphere）
		ServletContext sc = arg0.getServletContext();
		Config.put("System.ContainerInfo", sc.getServerInfo());// 连接池需要这个属性，所以先置
		
		Config.getJBossInfo();// 考虑JBoss
		Config.setPluginContext(true);
		Config.loadConfig();
		ExtendPluginProvider.getInstance().start();
		manager = CronManager.getInstance();
	}
}
