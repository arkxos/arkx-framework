package com.arkxos.framework.preloader;

import java.util.concurrent.locks.ReentrantLock;

import com.arkxos.framework.preloader.facade.FilterFacade;
import com.arkxos.framework.preloader.facade.HttpSessionListenerFacade;
import com.arkxos.framework.preloader.facade.ServletContextListenerFacade;
import com.arkxos.framework.preloader.facade.ServletFacade;

import jakarta.servlet.ServletException;

public class Reloader {
	
	public static boolean isReloading = false;
	private static ReentrantLock lock = new ReentrantLock();

	public static void reload() throws ServletException {
		lock.lock();
		try {
			isReloading = true;
			System.out.println("Preloader reload begin...");
			for (FilterFacade ff : FilterFacade.getInstances().values()) {
				ff.unloadClass();
			}

			for (ServletFacade sf : ServletFacade.getInstances().values()) {
				sf.unloadClass();
			}

			if (ServletContextListenerFacade.getInstance() != null) {
				ServletContextListenerFacade.getInstance().contextDestroyed(null);
				ServletContextListenerFacade.getInstance().unloadClass();
			}
			if (HttpSessionListenerFacade.getInstance() != null) {
				HttpSessionListenerFacade.getInstance().unloadClass();
			}
			
			System.setErr(PreClassLoader.err);
			System.setOut(PreClassLoader.out);
			
			ReferenceCleaner rc = new ReferenceCleaner(PreClassLoader.getInstance());
			rc.clearReferences();
			
			PreClassLoader.destory();
			System.gc();
			
			PreClassLoader.reloadAll();
			
			ServletContextListenerFacade.getInstance().loadListener();
			ServletContextListenerFacade.getInstance().contextInitialized(null);
			HttpSessionListenerFacade.getInstance().loadListener();
			for (FilterFacade ff : FilterFacade.getInstances().values()) {
				ff.loadFilter();
			}

			for (ServletFacade sf : ServletFacade.getInstances().values()) {
				sf.loadServlet();
			}

			System.out.println("Preloader reload end...");
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			isReloading = false;
			lock.unlock();
		}
	}

}
