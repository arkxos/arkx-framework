package org.ark.framework.jaf;

import java.io.IOException;
import java.lang.reflect.Method;

import org.ark.framework.security.PrivCheck;

import com.arkxos.framework.Config;
import io.arkx.framework.annotation.Path;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.ServletUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.cosyui.web.CookieData;
import com.arkxos.framework.cosyui.web.ResponseData;
import com.arkxos.framework.extend.ExtendManager;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * @class org.ark.framework.jaf.ActionFilter
 * 
 * @author Darkness
 * @date 2013-1-31 上午11:28:38 
 * @version V1.0
 */
public class ActionFilter implements Filter {
	
	public void init(FilterConfig config) throws ServletException {
		MainFilter.initFilter(config);
	}

	public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) rep;

			MainFilter.onFilterStart(request, response);
			try {
				String url = request.getServletPath();

				if (StringUtil.isEmpty(url)) {
					String requestURI = request.getRequestURI();
					String context = request.getContextPath();
					url = requestURI.substring(context.length(), requestURI.length());
				}

				if (url.indexOf("/") >= 0) {
					url = url.substring(url.lastIndexOf("/") + 1);
					if (url.endsWith(".zaction")) {
						String method = url.substring(0, url.lastIndexOf("."));
						invoke(request, response, method);

//						BlockingTransaction.clearTransactionBinding();

						return;
					}
				}
			} finally {
//				BlockingTransaction.clearTransactionBinding();
			}
			MainFilter.onFilterEnd(request, response);
		} finally {
			Current.clear();
		}
		Current.clear();
	}

	public static void invoke(HttpServletRequest request, HttpServletResponse response, String method) throws ServletException, IOException {
		Method m = Current.prepareMethod(request, response, method, new Class[] { ZAction.class });
		if (!PrivCheck.check(m, request, response)) {
			ExtendManager.invoke("org.ark.framework.AfterZActionPrivCheckFailedAction", new Object[] { request, response, m });
			return;
		}

		boolean flag = m.isAnnotationPresent(Path.class);
		if (flag) {
			Path p = m.getAnnotation(Path.class);
			String path = p.value();
			if (ObjectUtil.notEmpty(path)) {
				String url = request.getServletPath();
				url = url.substring(0, url.lastIndexOf("/") + 1);
				if (!path.startsWith("/")) {
					path = "/" + path;
				}
				if (!path.endsWith("/")) {
					path = path + "/";
				}
				if (!url.equals(path)) {
					try {
						response.getWriter().print("Invalid path for zaction : " + method);
					} catch (Exception localException) {
					}
					return;
				}
			}
		}

		ZAction action = new ZAction();
		CookieData cookies = new CookieData(request);
		action.setCookies(cookies);
		action.setRequest(request);
		action.setResponse(response);

		Mapx<String, String> params1 = ServletUtil.getParameterMap(request);
		Current.getRequest().putAll(params1);
		Current.invokeMethod(m, new Object[] { action });

		ResponseData params = Current.getResponse();
//		action.getCookies().write(request, response);

		response.setContentType("text/html");

		if ((Config.ServletMajorVersion == 2) && (Config.ServletMinorVersion == 3))
			response.setContentType("text/html;charset=" + Config.getGlobalCharset());
		else {
			response.setCharacterEncoding(Config.getGlobalCharset());
		}

		if (action.getForwardURL() != null) {
			for (String k : params.keySet()) {
				request.setAttribute(k, params.get(k));
			}
			request.getRequestDispatcher(action.getForwardURL()).forward(request, response);
		} else if (action.getRedirectURL() != null) {
			for (String k : params.keySet()) {
				request.setAttribute(k, params.get(k));
			}
			response.sendRedirect(action.getRedirectURL());
		} else if (!action.isBinaryMode()) {
			try {
				response.getWriter().print(action.getHTML());
			} catch (Exception localException1) {
			}
		} else {
			response.getOutputStream().close();
		}
	}

	public void destroy() {
	}
}