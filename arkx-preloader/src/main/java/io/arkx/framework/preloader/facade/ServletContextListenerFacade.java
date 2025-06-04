package io.arkx.framework.preloader.facade;

import java.util.ArrayList;

import io.arkx.framework.preloader.PreClassLoader;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;

public class ServletContextListenerFacade implements ServletContextListener {
	private String param;
	private ArrayList<ServletContextListener> list;
	private ServletContext context;
	private static ServletContextListenerFacade instance;

	public ServletContextListenerFacade() {
		instance = this;
	}

	public static ServletContextListenerFacade getInstance() {
		return instance;
	}

	public void contextInitialized(ServletContextEvent sce) {
		if (sce == null) {
			sce = new ServletContextEvent(this.context);
		}
		if (this.param == null) {
			init(sce.getServletContext());
		}
		if (this.list != null) {
			for (ServletContextListener listener : this.list) {
				try {
					listener.contextInitialized(sce);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		if (sce == null) {
			sce = new ServletContextEvent(this.context);
		}
		if (this.param == null) {
			init(sce.getServletContext());
		}
		if (this.list != null) {
			for (ServletContextListener listener : this.list) {
				try {
					listener.contextDestroyed(sce);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void init(ServletContext context) {
		this.param = context.getInitParameter("ServletContextListener");
		this.context = context;
		try {
			loadListener();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	public void unloadClass() {
		if (this.list != null) {
			this.list.clear();
		}
		this.list = null;
	}

	public void loadListener() throws ServletException {
		if (this.param == null) {
			return;
		}
		synchronized (this) {
			this.list = null;
			ArrayList<ServletContextListener> tmp = new ArrayList<>();
			String[] arrayOfString;
			int j = (arrayOfString = this.param.split("\\,")).length;
			for (int i = 0; i < j; i++) {
				String className = arrayOfString[i];
				className = className.trim();
				if (!className.equals("")) {
					try {
						Class<?> clazz = PreClassLoader.load(className);
						if ((clazz == null) || (!ServletContextListener.class.isAssignableFrom(clazz))) {
							throw new ServletException("Class " + className + " not found or not a ServletContextListener!");
						}
						ServletContextListener listener = (ServletContextListener) clazz.newInstance();
						tmp.add(listener);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			this.list = tmp;
		}
	}
}
