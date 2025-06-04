package org.ark.framework.jaf;

import java.io.IOException;

import org.ark.framework.extend.actions.AfterMainFilterAction;
import org.ark.framework.jaf.zhtml.ZhtmlCompileException;
import org.ark.framework.jaf.zhtml.ZhtmlManager;
import org.ark.framework.jaf.zhtml.ZhtmlRuntimeException;

import io.arkx.framework.Account;
import io.arkx.framework.Config;
import io.arkx.framework.Constant;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.Errorx;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.web.ResponseData;
import io.arkx.framework.extend.ExtendManager;
import io.arkx.framework.i18n.LangUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
import com.rapidark.preloader.facade.HttpSessionListenerFacade;
 * @class org.ark.framework.MainFilter
 * 框架过滤器
 * 
 * @author Darkness
 * @date 2012-8-6 下午10:05:02 
 * @version V1.0
 */
public class MainFilter implements Filter {
	
	private String[] NoFilterPaths;
	protected static boolean InitFlag = true;

	private static ServletContext context = null;

	public static ServletContext getContext() {
		return context;
	}
	
	/**
	 * 初始化过滤器，启动插件
	 * 
	 * @author Darkness
	 * @date 2012-8-6 下午10:07:10 
	 * @version V1.0
	 */
	public static void initFilter(FilterConfig config) {
		if (Config.ServletMajorVersion == 0) {
			context = config.getServletContext();
			Config.setValue("System.ContainerInfo", context.getServerInfo());
			Config.getJBossInfo();
			Config.ServletMajorVersion = context.getMajorVersion();
			Config.ServletMinorVersion = context.getMinorVersion();
			Config.setValue("App.Uptime", System.currentTimeMillis() + "");
			Config.setPluginContext(true);
//			ExtendManager.start();
			LogUtil.info("----" + Config.getAppCode() + "(" + LangUtil.get(Config.getAppName()) + "): Filter Initialized----");
		}
	}

	/**
	 * 初始化过滤器
	 */
	public void init(FilterConfig config) throws ServletException {
		initFilter(config);
		String paths = Config.getValue("App.NoFilterPath");
		if (StringUtil.isNotEmpty(paths)) {
			this.NoFilterPaths = paths.split(",");
			for (int i = 0; i < this.NoFilterPaths.length; i++) {
				String path = this.NoFilterPaths[i];
				if (!path.startsWith("/")) {
					path = "/" + path;
				}
				if (!path.endsWith("/")) {
					path = path + "/";
				}
				this.NoFilterPaths[i] = path;
			}
		}
	}

	/**
	 * 判断是否不是过滤路径
	 * 
	 * @author Darkness
	 * @date 2012-8-6 下午10:08:46 
	 * @version V1.0
	 */
	public boolean isNoFilterPath(String url) {
		if (this.NoFilterPaths == null) {
			return false;
		}
		url = url + "/";
		for (int i = 0; i < this.NoFilterPaths.length; i++) {
			if (url.indexOf(this.NoFilterPaths[i]) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 过滤请求，存储当前在线用户信息
	 * 
	 * @author Darkness
	 * @date 2012-8-6 下午10:09:52 
	 * @version V1.0
	 */
	public static void onFilterStart(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		/**
		 * 设置request、response编码
		 */
		if (!"false".equals(Config.getValue("SetRequestEncoding"))) {
			request.setCharacterEncoding(Config.getGlobalCharset());
		}
		if (!"false".equals(Config.getValue("SetResponseEncoding"))) {
			if ((Config.ServletMajorVersion == 2) && (Config.ServletMinorVersion == 3))
				response.setContentType("text/html;charset=" + Config.getGlobalCharset());
			else {
				response.setCharacterEncoding(Config.getGlobalCharset());
			}
		}

		Current.clear();

		if (!"true".equals(request.getParameter("_ARK_NOSESSION"))) {
			HttpSession session = request.getSession(true);
			if (session != null) {// 初始化用户数据，设置用户语言
				Account.UserData u = SessionListener.getUserDataFromSession(session);
				if (u == null) {
					boolean flag = true;
					if (Config.isDebugMode()) {
						Cookie[] cs = request.getCookies();
						if (cs != null) {
							for (int i = 0; i < cs.length; i++) {
								String contextPath;
								if (cs[i].getName().equals("JSESSIONID")) {
									u = Account.getCachedUser(cs[i].getValue());
									if (u != null) {
										flag = false;
										io.arkx.framework.cosyui.web.mvc.SessionListener.setSession(session.getId(), session);
										break;
									}
								}
							}
						}
					}
					if (flag) {
						u = createUserData(request);
					}
				}
				u.setSessionID(session.getId());
				if (io.arkx.framework.cosyui.web.mvc.SessionListener.getSession(session.getId()) == null) {
					io.arkx.framework.cosyui.web.mvc.SessionListener.setSession(session.getId(), session);
				}
				Account.setCurrent(u);
			} else {
				Account.setCurrent(createUserData(request));
			}

		}

		String contextPath = request.getContextPath();
		if (!contextPath.endsWith("/")) {
			contextPath = contextPath + "/";
		}
		if (Config.isComplexDepolyMode()) {
			Account.setValue("App.ContextPath", contextPath);
		}
		if (InitFlag) {
			Config.setValue("App.ContextPath", contextPath);
			InitFlag = false;
		}
//		Errorx.init();
	}

	public static Account.UserData createUserData(HttpServletRequest request) {
		Account.UserData u = new Account.UserData();
		try {
			String lang = LangUtil.getLanguage(request);
			Mapx map = LangUtil.getSupportedLanguages();
			if (map.size() < 1) {
				throw new RuntimeException("No supportd language found");
			}
			if (map.containsKey(lang))
				u.setLanguage(lang);
			else
				u.setLanguage(map.keySet().toArray()[0].toString());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return u;
	}

	/**
	 * 过滤请求
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chain) throws IOException, ServletException { // Byte
																																										// code:
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;
		String url = request.getServletPath();
		if (StringUtil.isEmpty(url)) {
			String requestURI = request.getRequestURI();
			String context = request.getContextPath();
			url = requestURI.substring(context.length(), requestURI.length());
		}

		if (isNoFilterPath(url)) {
			chain.doFilter(request, response);
			return;
		}
		onFilterStart(request, response);

		if ((!Config.isInstalled()) && (url.indexOf("Install.zhtml") < 0) && (url.indexOf("MainServlet.zhtml") < 0)) {
			try {
				ZhtmlManager.forward("/Install.zhtml", request, response, context);
			} catch (ZhtmlRuntimeException e) {
				e.printStackTrace();
			} catch (ZhtmlCompileException e) {
				e.printStackTrace();
			}
			Current.clear();
			return;
		}

		if ((url != null) && (url.indexOf("/MainServlet.zhtml") > 0) && (!url.equals("/MainServlet.zhtml"))) {
			RequestDispatcher rd = request.getRequestDispatcher("/MainServlet.zhtml");
			Current.clear();
			rd.forward(req, rep);
			return;
		}
		Current.setVariable("_PageExecuteStartTime", Long.valueOf(System.currentTimeMillis()));

		boolean isNeedExecute = true;
		Object[] results = ExtendManager.invoke(AfterMainFilterAction.ExtendPointID, new Object[] { request, response, chain });
		if(results != null && results.length > 0) {
			for (Object result : results) {
				boolean flag = Boolean.valueOf(result + "");
				if(!flag) {
					isNeedExecute = false;
					break;
				}
			}
		}
		
		if(isNeedExecute) {
			try {
				if (!ZhtmlManager.execute(url, request, response, context)) {
					try {
						chain.doFilter(request, response);
					} catch (Throwable e) {
						throw new ServletException(e);
					}
				}
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
		
//		BlockingTransaction.clearTransactionBinding();
		onFilterEnd(request, response);
		Current.clear();

	}

	public static void onFilterEnd(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			if ((!"true".equals(request.getParameter("_ARK_NOSESSION"))) && (!"true".equals(request.getAttribute("_ARK_NOSESSION"))) && (request.getSession(false) != null)) {
				Account.getCurrent().setSessionID(request.getSession(false).getId());
				request.getSession(false).setAttribute(Constant.UserAttrName, Account.getCurrent());
			}
		} catch (Exception localException) {
		}
		if (Current.getUIFacade() != null) {
//			Current.getUIFacade().getCookies().write(request, response);

			ResponseData r = Current.getUIFacade().getResponse();
			Mapx<String, String> map = r.getHeaders();
			for (String key : map.keySet()) {
				response.setHeader(key, (String) map.get(key));
			}
		}
		if (!Errorx.hasDealed()) {
			LogUtil.warn("Found error not dealed:" + Errorx.printString());
		}

		if (Current.containsVariable("_PageExecuteStartTime")) {
			Long start = (Long) Current.getVariable("_PageExecuteStartTime");
			long cost = System.currentTimeMillis() - start.longValue();
			if ((cost > 100L) && ("true".equals(Config.getValue("App.Log.PageServerCost"))) && (request.getServletPath().indexOf("/MainServlet.zhtml") < 0)) {
				LogUtil.info("ServerCost " + cost + "ms\t" + request.getServletPath());
			}
			if ((!"false".equals(Current.getVariable("_AddClientCostScript"))) 
					&& ("true".equals(Config.getValue("App.Log.PageClientCost")))
					&& (request.getServletPath().indexOf("/MainServlet.zhtml") < 0) 
					&& ("text/html".equals(response.getContentType())))
				response.getWriter().write("\n<script>var t="
								+ System.currentTimeMillis()
								+ ";var _readyCost=0;Page.onReady(function(){_readyCost=new Date().getTime()-t;});Page.onLoad(function(){var dc=new DataCollection();dc.add('ReadyCost',_readyCost);dc.add('URL',window.location.href);dc.add('Cost',new Date().getTime()-t);Server.sendRequest('org.ark.framework.FrameworkUI.sendPageCost',dc,null);});</script>");
		}
	}

	public void destroy() {
	}
}