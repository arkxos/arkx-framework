package org.ark.framework.jaf;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;

import org.ark.framework.jaf.clazz.ClassMethodFinder;
import org.ark.framework.jaf.spi.AliasMapping;
import org.ark.framework.orm.Schema;

import com.arkxos.framework.commons.collection.DataColumn;
import com.arkxos.framework.commons.collection.DataRow;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.exception.ServiceException;
import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.ServletUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.commons.util.lang.ClassUtil;
import com.arkxos.framework.cosyui.web.CookieData;
import com.arkxos.framework.cosyui.web.RequestData;
import com.arkxos.framework.cosyui.web.ResponseData;
import com.arkxos.framework.cosyui.web.UIFacade;
import com.arkxos.framework.data.db.DataCollection;
import com.arkxos.framework.i18n.LangMapping;
import com.rapidark.framework.Config;
import com.rapidark.framework.Constant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


/**
 * @class org.ark.framework.jaf.Current
 * 当前线程处理器，存储当前线程的UIFacade实例、当前线程的变量集合等
 * 
 * @author Darkness
 * @date 2012-8-5 下午4:49:35 
 * @version V1.0
 */
@Slf4j
public class Current {
	
//	private static Logger logger = log.getLogger(Current.class);

	private static ThreadLocal<Mapx<String, Object>> current = new ThreadLocal<Mapx<String, Object>>();
	public static final String UIFacadeKey = "_ARK_UIFACADE_KEY";
	public static final String TransactionKey = "_ARK_TRANSACTION_KEY";
	public static final String PlaceHolderContextKey = "_ARK_PLACEHOLDERCONTEXT_KEY";
	private static final String HttpRequestKey = "_ARK_HTTP_REQUEST_KEY";
	private static final String HttpResponseKey = "_ARK_HTTP_RESPONSE_KEY";

	/**
	 * 清除当前线程中的变量
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午6:42:27 
	 * @version V1.0
	 */
	public static void clear() {
		if (current.get() != null) {
			current.set(null);
//			Account.clear();
		}
	}

	/**
	 * 设置变量到当前线程中，如果值是Mapx<String, Object>，将mapx中的列表提取出来加上"key."前缀放到当前线程中，如：
	 * 
	 * 设置之前：
	 * __>show current.get();
	 * __>{}
	 * 
	 * Mapx<String, Object> personInfo = new Mapx<String, Object>();
	 * params.put("name", "darkness");
	 * params.put("age", "26");
	 * 
	 * Current.setVariable("person", personInfo);
	 * 
	 * 设置之后：
	 * __>show current.get();
	 * __>{"person.name": darkness, "person.age": "26"}
	 * 
	 * @param key 键
	 * @param value 值
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午6:43:31 
	 * @version V1.0
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setVariable(String key, Object value) {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			map = new Mapx<String, Object>();
			current.set(map);
		}
		if ((value instanceof Mapx)) {
			Mapx<String, Object> vmap = (Mapx) value;
			for (String k : vmap.keySet()) {
				map.put(key + "." + k, vmap.get(k));
			}
		}
		map.put(key, value);
	}

	/**
	 * 获取当前线程中的变量
	 * @param key 变量名称
	 * @return 变量值
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午6:58:57 
	 * @version V1.0
	 */
	public static Object getVariable(String key) {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			return null;
		}
		return map.get(key);
	}

	/**
	 * 获取当前线程中变量列表
	 * @return 当前线程中的变量列表
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:00:39 
	 * @version V1.0
	 */
	public static Mapx<String, Object> getAllVariables() {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			return null;
		}
		return map;
	}

	/**
	 * 检查当前线程中是否存在某个变量
	 * @param key 变量名
	 * @return 当前线程中是否存在该变量
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:02:11 
	 * @version V1.0
	 */
	public static boolean containsVariable(String key) {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			return false;
		}
		return map.containsKey(key);
	}

	/**
	 * 获取当前线程中的int变量值
	 * @param key 变量名
	 * @return int值
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:03:18 
	 * @version V1.0
	 */
	public static int getInt(String key) {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			return 0;
		}
		return map.getInt(key);
	}

	/**
	 * 获取当前线程中的long变量值
	 * @param key 变量名
	 * @return long值
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:04:06 
	 * @version V1.0
	 */
	public static long getLong(String key) {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			return 0L;
		}
		return map.getLong(key);
	}

	/**
	 * 获取当前线程中的String变量值
	 * @param key 变量名
	 * @return String值
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:04:45 
	 * @version V1.0
	 */
	public static String getString(String key) {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			return null;
		}
		return map.getString(key);
	}

	/**
	 * 初始化RequestImpl
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:05:32 
	 * @version V1.0
	 */
	public static RequestData initRequest(HttpServletRequest request) {
		if(request == null) {
			return new RequestData();
		}
		RequestData dc = getRequest();
		if (dc == null) {
			String data = request.getParameter(Constant.Data);
			dc = new RequestData();
			if (StringUtil.isNotEmpty(data)) {
				data = StringUtil.htmlDecode(data);
				dc.setURL(request.getParameter(Constant.URL));
				dc.setQueryString(request.getQueryString());
				dc.putAll(ServletUtil.getParameterMap(request));
				dc.remove(Constant.Data);
				dc.remove(Constant.URL);
				dc.remove(Constant.Method);
				dc.parseXML(data);
			} else {
				dc.setURL(request.getRequestURL() + "?" + request.getQueryString());
				dc.setQueryString(request.getQueryString());
				dc.putAll(ServletUtil.getParameterMap(request));
			}
			dc.setClientIP(ServletUtil.getRealIP(request));
			dc.setServerName(request.getServerName());
			dc.setPort(request.getServerPort());
			dc.setScheme(request.getScheme());
			dc.put("ContextPath", Config.getContextPath());
			
			Enumeration<?> e = request.getHeaderNames();
			while (e.hasMoreElements()) {
				String k = e.nextElement().toString();
				dc.getHeaders().put(k, request.getHeader(k));
			}
		}
		CookieData cookie = getCookies();
		if (cookie == null) {
			cookie = new CookieData(request);
		}
		dc.setCookies(cookie);
		PlaceHolderContext.getInstance(null, request);
		
		// 设置list循环当前行数据
		DataRow currentRow = (DataRow)request.getAttribute("currentRow");
		if(currentRow != null) {
			for (int i = 0; i < currentRow.getColumnCount(); i++) {
				DataColumn dataColumn = currentRow.getDataColumn(i);
				dc.put("currentRow." + dataColumn.getColumnName(), currentRow.get(i));
			}
		}
		
		return dc;
	}

	/**
	 * 准备UIFacade方法调用
	 * @param request HttpServletRequest实例
	 * @param response HttpServletResponse实例
	 * @param method 准备调用的UIFacade方法，如："com.ark.user.UserUI.initForm"
	 * @param types 准备调用的UIFacade方法的参数类型
	 * @return 准备调用的UIFacade方法
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:16:34 
	 * @version V1.0
	 */
	public static Method prepareMethod(HttpServletRequest request, HttpServletResponse response, String method, Class<?>[] types) {
		if (StringUtil.isEmpty(method))
			return null;
		try {
			int i = method.indexOf("?");
			String param = null;
			if (i > 0) {
				param = method.substring(i + 1);
				method = method.substring(0, i);
			}

			if (AliasMapping.exists(method)) {
				method = AliasMapping.get(method);
			}
			int index = method.lastIndexOf('.');
			if (index < 0) {
				throw new RuntimeException("Invalid Method:" + method);
			}
			String className = method.substring(0, index);

			Class<?> c = Class.forName(className);
			Object o = c.newInstance();
			UIFacade page = (UIFacade) o;

			RequestData dc = initRequest(request);
			dc.setClassName(className);
//			page.setCookies(dc.getCookies());
			page.setRequest(dc);
			if (ObjectUtil.notEmpty(param)) {
				Mapx<String, String> paramMap = StringUtil.splitToMapx(param, "&", "=");
				dc.putAll(paramMap);
			}
			Mapx<String, Object> map = current.get();
			if (map == null) {
				map = new Mapx<String, Object>();
				current.set(map);
			}
			map.put(UIFacadeKey, page);
			map.put(HttpRequestKey, request);
			map.put(HttpResponseKey, response);

			method = method.substring(index + 1);
			return ClassMethodFinder.getMethod(c, method, types);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Method " + method + " isn't marked by @Priv or class isn't found!");
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			DataCollection dcResponse = new DataCollection();
			dcResponse.put("_ARK_STATUS", Integer.valueOf(0));
			String msg = "Error occurs in Current.prepareMethod:" + method;
			LogUtil.warn(msg);
			e.printStackTrace();
			dcResponse.put("_ARK_MESSAGE", msg);
			try {
				response.getWriter().write(dcResponse.toXML());
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 查找method对应的Method
	 * @param method 方法名称
	 * @param types 方法的参数类型列表
	 * @return 找到的方法
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:21:18 
	 * @version V1.0
	 */
	public static Method findMethod(String method, Class<?>[] types) {
		if (StringUtil.isEmpty(method))
			return null;
		try {
			if (AliasMapping.exists(method)) {
				method = AliasMapping.get(method);
			}
			int index = method.lastIndexOf('.');
			String className = method.substring(0, index);

			UIFacade p = getUIFacade();
			// 方法不属于当前UIFacade，重新设置当前UIFacade
			if (!p.getClass().getName().equals(className)) {
				Class<?> c = Class.forName(className);
				UIFacade p2 = (UIFacade) c.newInstance();
				p2.Request = p.Request;
				p2.Response = p.Response;
				p2.Cookies = p.Cookies;
				p = p2;
				
				current.get().put(UIFacadeKey, p);
			}
			
			Class<?> c = Class.forName(className);
			method = method.substring(index + 1);
			return ClassMethodFinder.getMethod(c, method, types);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 动态调用指定的方法
	 * @param m 动态调用的方法
	 * @param args 方法的参数列表
	 * @return 调用方法后的返回值
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:23:25 
	 * @version V1.0
	 */
	public static Object invokeMethod(Method m, Object[] args) {
		try {
			String LoginClass = Config.getValue("App.LoginClass");
			String className = m.getDeclaringClass().getName();

			if ((!UIFacade.class.isAssignableFrom(m.getDeclaringClass())) && (!className.equals(LoginClass))) {
				LogUtil.warn(className + "." + m.getName() + " hasn't @Priv annotation,invokeMethod is forbidden");
				return false;
			}

			if (!Modifier.isStatic(m.getModifiers())) {
				UIFacade p = getUIFacade();
				
				args = fixMethodParamsValue(m, args);
				Object result = m.invoke(p, args);
				
				if(Current.getResponse() != null) {
					Current.getResponse().setSuccessMessage(LangMapping.get("Common.ExecuteSuccess"));	
				}
				
				return result;
			}
			
			args = fixMethodParamsValue(m, args);
			Object result = m.invoke(null, args);
			Current.getResponse().setSuccessMessage(LangMapping.get("Common.ExecuteSuccess"));
			return result;
		} catch (InvocationTargetException e) {
			if(e.getTargetException() instanceof ServiceException){
				ServiceException serviceException = (ServiceException)e.getTargetException();
				Current.getResponse().setFailedMessage(serviceException.getMessage());
				Current.getResponse().put("serviceExceptionCode", serviceException.getCode());
			} else {
				e.printStackTrace();
				Current.getResponse().setFailedMessage(e.getTargetException().getMessage());
			}
			log.error(getRequest()+"");
//			try {
//				CurrentConnection.getCurrentThreadConnection().rollback();
//				CurrentConnection.clearTransactionBindingWithCommit();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
		} catch (Throwable e) {
			e.printStackTrace();
			Current.getResponse().setFailedMessage(e.getMessage());
			log.error(getRequest() + "");
//			try {
//				CurrentConnection.getCurrentThreadConnection().rollback();
//				CurrentConnection.clearTransactionBindingWithCommit();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
		}
		return null;
	}

	private static Object[] fixMethodParamsValue(Method m, Object[] args) {

		Class<?>[] cs = m.getParameterTypes();
		Object[] result = new Object[cs.length];

		String[] paramNames = ClassUtil.getMethodParamNames(m.getDeclaringClass(), m.getName());
		for (int j = 0; j < cs.length; j++) {
			LogUtil.debug("方法第" + j + "个参数" + paramNames[j] + "[" + cs[j].getName() + "]");

			boolean useArg = false;
			if ((args != null) && (args.length > j) && (cs[j].isInstance(args[j]))) {
				result[j] = args[j];
				useArg = true;

				LogUtil.debug("匹配传进来的参数成功，直接采用传进来的参数值：" + args[j]);
			} else if (!useArg) {
				LogUtil.debug("匹配失败，将从request中获取名称为[" + paramNames[j] + "]的值...");

				RequestData request = getRequest();
				request.putAll(getAllVariables());
				Object[] keys = request.keyArray().toArray();
				for (Object key : keys) {
					if (paramNames[j].equalsIgnoreCase(key.toString())) {
						Object paramValue = request.get(key);

						LogUtil.debug("request中存在名称为[" + paramNames[j] + "]的值：" + paramValue);

						result[j] = paramValue;
						break;
					}
				}

				if (Schema.class.isAssignableFrom(cs[j])) {
					Schema schema = null;
					try {
						schema = (Schema) cs[j].newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					schema.setValue(getRequest());
					result[j] = schema;
				} else if(Mapx.class.isAssignableFrom(cs[j])) {
					result[j] = getRequest();
				}
				
				if (HttpServletRequest.class.isAssignableFrom(cs[j])) {
					result[j] = getHttpRequest();
				} else if(HttpServletResponse.class.isAssignableFrom(cs[j])) {
					result[j] = getHttpResponse();
				}
			}
		}
		
		return result;
	}

	/**
	 * 动态调用指定的方法
	 * @param method 动态调用的方法
	 * @param args 方法的参数列表
	 * @return 调用方法后的返回值
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:25:55 
	 * @version V1.0
	 */
	public static Object invokeMethod(String method, Object[] args) {
		try {
			if (AliasMapping.exists(method)) {
				method = AliasMapping.get(method);
			}
			int index = method.lastIndexOf('.');
			String className = method.substring(0, index);
			method = method.substring(index + 1);

			Class<?> c = Class.forName(className);
			Method[] ms = c.getMethods();
			Method m = null;
			boolean flag = false;
			for (int i = 0; i < ms.length; i++) {
				m = ms[i];
				if (m.getName().equals(method)) {
					Class<?>[] cs = m.getParameterTypes();
					if ((args != null) && (args.length == cs.length)) {
						for (int j = 0; j < cs.length; j++) {
							if (!cs[j].isInstance(args[j])) {
								break;
							}
						}
						flag = true;
						break;
					}

					if ((args == null) && ((cs == null) || (cs.length == 0))) {
						flag = true;
						break;
					}
				}
			}
			if (!flag) {
				throw new RuntimeException("Suitable arguments not found:" + className + "." + method);
			}
			invokeMethod(m, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取当前线程中的UIFacade
	 * @return 当前线程中的UIFacade
	 * @author Darkness
	 * @date 2012-8-5 下午7:14:57 
	 * @version V1.0
	 */
	public static UIFacade getUIFacade() {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			return null;
		}
		return (UIFacade) map.get(UIFacadeKey);
	}

	/**
	 * 获取当前线程中的事务管理器
	 * @return 获取当前线程中的事务管理器
	 * @author Darkness
	 * @date 2012-8-5 下午7:14:14 
	 * @version V1.0
	 */
//	public static Transaction getTransaction() {
//		Mapx<String, Object> map = current.get();
//		if (map == null) {
//			map = new Mapx<String, Object>();
//			current.set(map);
//		}
//		Transaction tran = (Transaction) map.get(TransactionKey);
//		if (tran == null) {
//			tran = new Transaction();
//			map.put(TransactionKey, tran);
//		}
//		return tran;
//	}

	/**
	 * 获取当前线程中的RequestImpl实例
	 * @return 当前线程中的RequestImpl实例
	 * @author Darkness
	 * @date 2012-8-5 下午7:12:43 
	 * @version V1.0
	 */
	public static RequestData getRequest() {
		UIFacade p = getUIFacade();
		if (p == null) {
			return null;
		}
		return p.Request;
	}
	
	public static HttpServletRequest getHttpRequest() {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			return null;
		}
		return (HttpServletRequest) map.get(HttpRequestKey);
	}
	
	public static HttpServletResponse getHttpResponse() {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			return null;
		}
		return (HttpServletResponse) map.get(HttpResponseKey);
	}

	/**
	 * 获取当前线程中的ResponseImpl实例
	 * @reutn 当前线程中的ResponseImpl实例
	 * @author Darkness
	 * @date 2012-8-5 下午7:12:21 
	 * @version V1.0
	 */
	public static ResponseData getResponse() {
		UIFacade p = getUIFacade();
		if (p == null) {
			return null;
		}
		return p.Response;
	}

	/**
	 * 获取当前线程中的cookie集合
	 * @return cookie集合
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:12:00 
	 * @version V1.0
	 */
	public static CookieData getCookies() {
		UIFacade p = getUIFacade();
		if (p == null) {
			return null;
		}
		return p.getCookies();
	}

	/**
	 * 获取当前线程中指定的cookie
	 * @param name cookie的名称
	 * @return cookie的值
	 * 
	 * @author Darkness
	 * @date 2012-8-5 下午7:10:46 
	 * @version V1.0
	 */
	public static String getCookie(String name) {
		UIFacade p = getUIFacade();
		if (p == null) {
			return null;
		}
		return p.getCookies().getCookie(name);
	}

	/**
	 * 获取当前线程中的PlaceHolderContext上下文
	 * @return 当前线程中的PlaceHolderContext上下文
	 * @author Darkness
	 * @date 2012-8-5 下午7:10:12 
	 * @version V1.0
	 */
	public static PlaceHolderContext getPlaceHolderContext() {
		Mapx<String, Object> map = current.get();
		if (map == null) {
			return null;
		}
		return (PlaceHolderContext) map.get(PlaceHolderContextKey);
	}
}