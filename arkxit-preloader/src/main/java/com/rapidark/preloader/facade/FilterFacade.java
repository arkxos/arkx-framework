package com.rapidark.preloader.facade;

import java.io.IOException;
import java.util.HashMap;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import com.rapidark.preloader.PreClassLoader;
import com.rapidark.preloader.Reloader;

public class FilterFacade implements Filter {
	private FilterConfig config;
	private Filter filter;
	private static HashMap<String, FilterFacade> instances = new HashMap<>();

	public static HashMap<String, FilterFacade> getInstances() {
		return instances;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		loadFilter();
		instances.put(config.getInitParameter("class"), this);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if ((this.filter != null) && (!Reloader.isReloading)) {
			this.filter.doFilter(request, response, chain);
		} else if (Reloader.isReloading) {
			response.getWriter().print("Preloader.Reloading...");
		}
	}

	@Override
	public void destroy() {
		if (this.filter != null) {
			this.filter.destroy();
			PreClassLoader.destory();
		}
	}
	
	public void unloadClass() {
		if (this.filter != null) {
			try {
				this.filter.destroy();
			} catch (Throwable localThrowable) {
			}
		}
		this.filter = null;
	}

	public void loadFilter() throws ServletException {
		String className = this.config.getInitParameter("class");
		if (className == null) {
			return;
		}
		synchronized (this) {
			try {
				Class<?> clazz = PreClassLoader.load(className);
				if ((clazz == null) || (!Filter.class.isAssignableFrom(clazz))) {
					throw new ServletException("Class " + className + " not found or not a Filter!");
				}
				this.filter = ((Filter) clazz.newInstance());
				this.filter.init(this.config);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
