package com.rapidark.preloader.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import com.rapidark.preloader.PreClassLoader;
import com.rapidark.preloader.Reloader;

public class HttpSessionListenerFacade implements HttpSessionListener {
	private String param;
	private List<HttpSessionListener> list;
	private static Map<String, HttpSession> map = new ConcurrentHashMap();
	private static HttpSessionListenerFacade instance;

	public HttpSessionListenerFacade() {
		instance = this;
	}

	public static Map<String, HttpSession> getMap() {
		return map;
	}

	public static HttpSessionListenerFacade getInstance() {
		return instance;
	}

	public void sessionCreated(HttpSessionEvent se) {
		if (this.param == null) {
			init(se.getSession().getServletContext());
		}
		HttpSession hs = se.getSession();
		map.put(hs.getId(), hs);
		if ((this.list != null) && (!Reloader.isReloading)) {
			for (HttpSessionListener listener : this.list) {
				listener.sessionCreated(se);
			}
		}
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		if (this.param == null) {
			init(se.getSession().getServletContext());
		}
		if ((this.list != null) && (!Reloader.isReloading)) {
			for (HttpSessionListener listener : this.list) {
				listener.sessionDestroyed(se);
			}
		}
		map.remove(se.getSession().getId());
	}

	private void init(ServletContext context) {
		this.param = context.getInitParameter("HttpSessionListener");
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
		if (map != null) {
			try {
				for (Object obj : map.values()) {
					((HttpSession) obj).invalidate();
				}
			} catch (Throwable localThrowable) {
			}
			map.clear();
		}
	}

	public void loadListener() throws ServletException {
		if (this.param == null) {
			return;
		}
		synchronized (this) {
			map = new ConcurrentHashMap();
			ArrayList<HttpSessionListener> tmp = new ArrayList();
			String[] arrayOfString;
			int j = (arrayOfString = this.param.split("\\,")).length;
			for (int i = 0; i < j; i++) {
				String className = arrayOfString[i];
				className = className.trim();
				if (!className.equals("")) {
					try {
						Class<?> clazz = PreClassLoader.load(className);
						if ((clazz == null) || (!HttpSessionListener.class.isAssignableFrom(clazz))) {
							throw new ServletException("Class " + className + " not found or not a HttpSessionListener!");
						}
						HttpSessionListener listener = (HttpSessionListener) clazz.newInstance();
						tmp.add(listener);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			this.list = tmp;
		}
	}

	public static HttpSession getSession(String id) {
		return (HttpSession) map.get(id);
	}

	public static void setSession(String id, HttpSession session) {
		map.put(id, session);
	}
}
