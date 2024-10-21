package com.rapidark.framework.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rapidark.framework.Account;
import com.rapidark.framework.Account.UserData;
import com.rapidark.framework.Config;
import com.rapidark.framework.Constant;
import com.rapidark.framework.Current;
import com.rapidark.framework.commons.collection.ConcurrentMapx;
import com.rapidark.framework.commons.util.Errorx;
import com.rapidark.framework.commons.util.LogUtil;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.config.SetRequestEncoding;
import com.rapidark.framework.config.SetResponseEncoding;
import com.rapidark.framework.cosyui.web.mvc.SessionListener;

/**
 *  
import com.rapidark.preloader.PreClassLoader;
import com.rapidark.preloader.facade.HttpSessionListenerFacade;
 * @author Darkness
 * @date 2016年12月23日 下午3:18:30
 * @version V1.0
 */
@WebFilter(filterName = "LoginFilter",
	urlPatterns = {"/services/*","*.do"},
		initParams = {@WebInitParam(name = "noFilterPath",value = "/ajax/invoke;")})
public class LoginFilter implements Filter {

	protected static boolean initFlag = true;
	protected static ConcurrentMapx<Thread, ClassLoader> httpThreads = new ConcurrentMapx<>();
	protected static long lastThreadCheckTime = 0;
	
	private String[] notFilterPaths;
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		String paths = config.getInitParameter("noFilterPath");
		if (StringUtil.isNotEmpty(paths)) {
			notFilterPaths = paths.split(",");
			for (int i = 0; i < notFilterPaths.length; i++) {
				String path = notFilterPaths[i];
				if (!path.startsWith("/")) {
					path = "/" + path;
				}
				if (!path.endsWith("/")) {
					path = path + "/";
				}
				notFilterPaths[i] = path;
			}
		}
	}

	public boolean isNoFilterPath(String url) {
		if (notFilterPaths == null) {
			return false;
		}
		for (String noFilterPath : notFilterPaths) {
			if (url.indexOf(noFilterPath) >= 0) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chain) throws IOException, ServletException {
		long t = System.currentTimeMillis();
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;
		String requestURI = request.getRequestURI();
		String context = request.getContextPath();
		String url = requestURI.substring(context.length(), requestURI.length());// 以/开头
		url = url.replace("//", "/");
		if (ObjectUtil.isEmpty(url)) {
			response.sendRedirect(context + "/");
			return;
		}

		if (isNoFilterPath(url)) {
			return;
		}

		// 初始化路径，需要考虑集群的情况，需要考虑内外网路径不一致的情况
		// 注意如果遇到以下情况的时候cookie设值得到的path会有所不同：本地访问路径是http://IP/rapidark，而外网访问路径是:http://域名
		if (Config.isComplexDepolyMode() || initFlag) {
			if (context.length() == 0 || context.charAt(context.length() - 1) != '/') {
				context = context + "/";
			}
			Account.setValue("App.ContextPath", context);
			if (initFlag) {
				Config.setValue("App.ContextPath", context);
				initFlag = false;
			}
		}
		if (SetRequestEncoding.getValue()) {
			request.setCharacterEncoding(Config.getGlobalCharset());
		}
		if (SetResponseEncoding.getValue()) {
			if (Config.getServletMajorVersion() == 2 && Config.getServletMinorVersion() == 3) {
				response.setContentType("text/html;charset=" + Config.getGlobalCharset());
			} else {
				response.setCharacterEncoding(Config.getGlobalCharset());
			}
		}
		Current.prepare(request, response);
		try {
//			if (Thread.currentThread().getContextClassLoader() != PreClassLoader.getInstance()) {
				httpThreads.put(Thread.currentThread(), Thread.currentThread().getContextClassLoader());
//				Thread.currentThread().setContextClassLoader(PreClassLoader.getInstance());
				if (System.currentTimeMillis() - lastThreadCheckTime > 300000) {// 5分钟检查一次
					for (Thread thread : httpThreads.keySet()) {
						if (!thread.isAlive()) {
							httpThreads.remove(thread);
						}
					}
					lastThreadCheckTime = System.currentTimeMillis();
				}
//			}

			tryRestoreSession(request, response);

			chain.doFilter(request, response);
			
			try {
				if (!"true".equals(request.getParameter(Constant.NoSession)) && !"true".equals(request.getAttribute(Constant.NoSession))) {
					UserData ud = Account.getCurrent();
					if (ud != null) {// 已经置了值了
						HttpSession session = request.getSession(false);
						if (session != null && session.getAttribute(Constant.UserAttrName) != ud) {
							ud.setSessionID(session.getId());
							session.setAttribute(Constant.UserAttrName, ud);// 必须在此重置，因此User对象可能被重置了
						}
					}
				}
			} catch (Exception e) {
				// 有可能已经被invalidate了
			}
			if (!Errorx.hasDealed()) {
				LogUtil.warn("Error not dealed:" + Errorx.printString());
			}
			Account.tryCacheCurrentUserData();
		} catch (RuntimeException e) {// 集中异常处理
			boolean catched = false;
			for (IExceptionCatcher ec : ExceptionCatcherService.getInstance().getAll()) {
				for (Class<?> c : ec.getTargetExceptionClass()) {
					if (c.isInstance(e)) {
						ec.doCatch(e, request, response);
						catched = true;
					}
				}
			}
			if (!catched) {
				e.printStackTrace();
				throw e;
			}
		} finally {
			Current.clear();// 确保Current中的数据被清空
			t = System.currentTimeMillis() - t;
			if (t > 100) {
				System.out.println("URL " + url + " cost " + t + "ms.");
			}
		}
	}

	@Override
	public void destroy() {
	}
	
	private void tryRestoreSession(HttpServletRequest request, HttpServletResponse response) {
		// 准备用户会话数据
		if ("true".equals(request.getParameter(Constant.NoSession))) {
			return;
		}
		HttpSession session = request.getSession(false);
		UserData u = null;
		if (session != null) {
			u = SessionListener.getUserDataFromSession(session);
			if (u == null) {
				if (Config.isDebugMode()) {
					u = getCachedUserData(request);
					if (u != null) {
						SessionListener.setSession(session.getId(), session);
					}
				}
			}
		} else {
			if (Config.isDebugMode()) {
				u = getCachedUserData(request);
				if (u != null) {
					session = request.getSession(true);
					SessionListener.setSession(session.getId(), session);
					u.setSessionID(session.getId());
				}
			}
		}
		Account.setCurrent(u);
	}

	private UserData getCachedUserData(HttpServletRequest request) {
		if (Config.isDebugMode()) {
			Cookie[] cs = request.getCookies();
			if (cs != null) {
				for (Cookie element : cs) {
					if (element.getName().equals(Constant.SessionIDCookieName)) {
						return Account.getCachedUser(element.getValue());
					}
				}
			}
		}
		return null;
	}

}
