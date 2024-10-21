package com.rapidark.preloader.facade;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rapidark.preloader.PreClassLoader;
import com.rapidark.preloader.Reloader;

public class ServletFacade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ServletConfig config;
	private Servlet servlet;
	private static HashMap<String, ServletFacade> instances = new HashMap<>();

	public static HashMap<String, ServletFacade> getInstances() {
		return instances;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		loadServlet();
		instances.put(config.getInitParameter("class"), this);
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if ((this.servlet != null) && (!Reloader.isReloading)) {
			this.servlet.service(request, response);
		}
	}

	@Override
	public void destroy() {
		if (this.servlet != null) {
			this.servlet.destroy();
		}
	}
	
	public void unloadClass() {
		if (this.servlet != null) {
			try {
				this.servlet.destroy();
			} catch (Throwable localThrowable) {
			}
		}
		this.servlet = null;
	}

	public void loadServlet() throws ServletException {
		String className = this.config.getInitParameter("class");
		if (className == null) {
			return;
		}
		synchronized (this) {
			try {
				// spring PropertyEditorRegistrySupport 默认取当前线程中的classLoader，web环境下，如果不设置，默认取web容器的classLoader
				Thread.currentThread().setContextClassLoader(PreClassLoader.getInstance());
				Class<?> clazz = PreClassLoader.load(className);
				if ((clazz == null) || (!Servlet.class.isAssignableFrom(clazz))) {
					throw new ServletException("Class " + className + " not found or not a servlet!");
				}
				this.servlet = ((Servlet) clazz.newInstance());
				this.servlet.init(this.config);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
