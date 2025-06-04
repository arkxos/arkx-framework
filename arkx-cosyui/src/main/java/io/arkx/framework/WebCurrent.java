package io.arkx.framework;

import java.util.Map;

import io.arkx.framework.Account.UserData;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.Errorx;
import io.arkx.framework.commons.util.ServletUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.core.method.IMethodLocator;
import io.arkx.framework.cosyui.template.AbstractExecuteContext;
import io.arkx.framework.cosyui.web.CookieData;
import io.arkx.framework.cosyui.web.RequestData;
import io.arkx.framework.cosyui.web.ResponseData;
import io.arkx.framework.cosyui.web.UIFacade;
import io.arkx.framework.cosyui.web.mvc.Dispatcher;
import io.arkx.framework.cosyui.web.mvc.IURLHandler;
import io.arkx.framework.cosyui.zhtml.ZhtmlExecuteContext;
import io.arkx.framework.cosyui.zhtml.ZhtmlManagerContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 当前数据类，访问当前http请求中的相关对象的便捷方式
 * 
 */
public class WebCurrent extends Current {




	/**
	 * 准备Current数据
	 */
	public static void prepare(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		WebCurrentData cd = (WebCurrentData)current.get();
		if (cd == null) {
			cd = new WebCurrentData();
			current.set(cd);
		}
		if (cd.handler == null && servletRequest != null) {
			RequestData request = WebCurrent.getRequest();
			String data = servletRequest.getParameter(Constant.Data);
			
			String realPath = servletRequest.getSession().getServletContext().getRealPath("/");  
			request.setRealPath(realPath);
			request.setServletRequest(servletRequest);
			WebCurrent.getResponse().setServletResponse(servletResponse);

			request.setQueryString(servletRequest.getQueryString());
			request.setHttpMethod(servletRequest.getMethod());
			request.putAll(ServletUtil.getParameterMap(servletRequest));
			
			// TODO 这边不能读取，因为从request中读取inputstream后，springmvc就获取不到了
//			String requestBody = ServletUtil.readRequestBody(servletRequest);
//			if(requestBody.startsWith("{")) {
//				System.out.println("requestBody:" + requestBody);
//				CaseIgnoreMapx paramsMap = JSON.parseObject(requestBody, CaseIgnoreMapx.class);
//				request.putAll(paramsMap);
//			}
			if (StringUtil.isNotEmpty(data)) {
				request.setURL(servletRequest.getParameter(Constant.URL));
				request.remove(Constant.Data);
				request.remove(Constant.URL);
				request.remove(Constant.Method);
				request.remove(Constant.DataFormat);
				if (data.startsWith("<?xml")) {
					request.parseXML(data);
				} else if (data.startsWith("{") || data.startsWith("[")) {
					request.parseJSON(data);
				}
			} else {
				if (StringUtil.isEmpty(servletRequest.getQueryString())) {
					request.setURL(servletRequest.getRequestURL().toString());
				} else {
					request.setURL(servletRequest.getRequestURL().append("?").append(servletRequest.getQueryString()).toString());
				}
			}
			request.setClientIP(ServletUtil.getRealIP(servletRequest));
			request.setServerName(servletRequest.getServerName());
			request.setPort(servletRequest.getServerPort());
			request.setScheme(servletRequest.getScheme());
			HttpSession session = servletRequest.getSession(false);
			if (session != null) {
				request.setSessionID(session.getId());
			}
		}
	}

	/**
	 * 获取当前线程中的事务
	 * 
	 * @return
	 */
//	public static Transaction getTransaction() {
//		CurrentData data = current.get();
//		if (data == null) {
//			data = new CurrentData();
//			current.set(data);
//		}
//		Transaction tran = data.transaction;
//		if (tran == null) {
//			tran = new Transaction();
//			data.transaction = tran;
//		}
//		return tran;
//	}

	/**
	 * 获取当前请求中的RequestImpl对象
	 * 
	 * @return
	 */
	public static RequestData getRequest() {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			return null;
		}
		return data.request;
	}

	/**
	 * 获取当前请求中的UIFacade对象
	 * 
	 * @return
	 */
	public static UIFacade getUIFacade() {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			return null;
		}
		return data.facade;
	}

	/**
	 * 设置当前的UIFacade对象
	 */
	public static void setUIFacade(UIFacade facade) {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			data = new WebCurrentData();
			current.set(data);
		}
		data.facade = facade;
	}

	/**
	 * 获取当前请求中的ResponseImpl对象
	 * 
	 * @return
	 */
	public static ResponseData getResponse() {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			return null;
		}
		return data.response;
	}

	/**
	 * 获取当前请求中的CookieImpl对象
	 * 
	 * @return
	 */
	public static CookieData getCookies() {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			return null;
		}
		return data.request.getCookies();
	}

	/**
	 * 获取当前请求中的单个Cookie值
	 * 
	 * @return
	 */
	public static String getCookie(String name) {
		CookieData cc = getCookies();
		if (cc == null) {
			return null;
		}
		return cc.getCookie(name);
	}

	/**
	 * 获取http变量解析
	 * 
	 * @return
	 */
	public static AbstractExecuteContext getExecuteContext() {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			return null;
		}
		if (data.executeContext == null) {
			data.executeContext = new ZhtmlExecuteContext(ZhtmlManagerContext.getInstance(), null, null);
		}
		return data.executeContext;
	}

	/**
	 * 设置当前的模板执行上下文
	 */
	public static void setExecuteContext(AbstractExecuteContext context) {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			data = new WebCurrentData();
			current.set(data);
		}
		data.executeContext = context;
	}

	/**
	 * 设置用户
	 */
	public static void setUser(UserData ud) {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			data = new WebCurrentData();
			current.set(data);
		}
		data.userData = ud;
	}

	/**
	 * 获取用户
	 */
	public static UserData getUser() {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			return null;
		}
		return data.userData;
	}
	
	public static boolean isAdmin() {
		return WebCurrent.getUser().getUserName().equals("admin");
	}

	/**
	 * 获取调度
	 * 
	 * @return
	 */
	public static Dispatcher getDispatcher() {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			return null;
		}
		return data.dispatcher;
	}

	/**
	 * 设置URLHandler
	 * 
	 * @param handler
	 */
	public static void setURLHandler(IURLHandler handler) {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			data = new WebCurrentData();
			current.set(data);
		}
		data.handler = handler;
	}

	/**
	 * 获取URLHandler
	 * 
	 * @return
	 */
	public static IURLHandler getURLHandler() {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			return null;
		}
		return data.handler;
	}

	/**
	 * 设置URLHandler
	 * 
	 * @param handler
	 */
	public static void setMethod(IMethodLocator method) {
		WebCurrentData data =(WebCurrentData)current.get();
		if (data == null) {
			data = new WebCurrentData();
			current.set(data);
		}
		data.method = method;
	}

	/**
	 * 获取当前方法，注意，一次请求可能会调用多个方法，因此在一次http请求中本方法可能会返回不同的结果
	 * 
	 * @return
	 */
	public static IMethodLocator getMethod() {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			return null;
		}
		return data.method;
	}

	/**
	 * 获取错误
	 * 
	 * @return
	 */
	public static Errorx getErrorx() {
		WebCurrentData data = (WebCurrentData)current.get();
		if (data == null) {
			data = new WebCurrentData();
			current.set(data);
		}
		if (data.errorx == null) {
			data.errorx = new Errorx();
		}
		return data.errorx;
	}

	/**
	 * 获取当前数据
	 * 
	 * @return
	 */
	public static WebCurrentData getCurrentData() {
		return (WebCurrentData)current.get();
	}



}
