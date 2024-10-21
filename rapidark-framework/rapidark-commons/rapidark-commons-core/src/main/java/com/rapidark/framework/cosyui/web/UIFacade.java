package com.rapidark.framework.cosyui.web;

import com.rapidark.framework.Current;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.cosyui.web.mvc.Dispatcher;
import com.rapidark.framework.data.jdbc.Session;
import com.rapidark.framework.data.jdbc.SessionFactory;
import com.rapidark.framework.i18n.Lang;
import com.rapidark.framework.json.JSONArray;
import com.rapidark.framework.json.JSONObject;

/**
 * 前端界面请求绑定界面门面
 * 一个页面或模块中的所有后台方法的集合。<br>
 * 所有响应JavaScript中Server.sendRequest()方法的后台类都必须继承本类<br>
 * 
 * 
 * 设置结果集页面显示：<br/>
 * <br/>
 * Server.sendRequest(this.uiFacade + ".getInfoListByIds", dc, function(response)<br/> 
 * &nbsp;&nbsp;&nbsp;&nbsp;if(response.Status == 1) {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;var dataTable = response.get("infoList");<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 * });<br/>
 * <br/>
 * DataTable dt = new DataTable();<br/>
 * this.$S("infoList", dt);<br/>
 * 
 * @author Darkness
 * @date 2012-8-5 下午11:08:54 
 * @version V1.0
 */
public abstract class UIFacade implements IUIFacade {
	/**
	 * 响应本次请求的数据容器，放在Response中的数据在JavaScript中可以用Response.get()获取到
	 */
	public ResponseData Response;

	/**
	 * 本次请求的所有参数，包括URL和表单参数，以及部分Http头
	 */
	public RequestData Request;

	/**
	 * 本次请求发送的所有Cookie
	 */
	public CookieData Cookies;

	/**
	 * 从Current中初始化内置的Request,Response,Cookie的值。<br>
	 * （ 本方法通常用于从UI类A中调用UI类B中的方法时初始化B中的内置对象）
	 */
	public UIFacade() {
		Request = Current.getRequest();
		Cookies = Current.getCookies();
		Response = Current.getResponse();
		if (Response == null) {
			Response = new ResponseData();
		}
		if (Cookies == null) {
			Cookies = new CookieData();
		}
		if (Request == null) {
			Request = new RequestData();
		}
	}

	public void setRequest(RequestData r) {
		this.Request = r;
	}
	
	public String $V(String id) {
		return getValue(id);
	}
	
	/**
	 * 从Request中获取一个字符串，如果Request中没有，则尝试从Response中获取
	 */
	public String getValue(String id) {
		Object v = Request.get(id);
		if (ObjectUtil.isEmpty(v)) {
			v = Response.get(id);
		}
		if (v == null) {
			return null;
		}
		if (v instanceof JSONArray) {
			return StringUtil.join((JSONArray) v);
		}
		return v.toString();
	}

	/**
	 * 设置Response中的变量，相当于Response.put()
	 */
	public IUIFacade $S(String id, Object value) {
		Response.put(id, value);
		return this;
	}

	/**
	 * 获取当前响应数据
	 */
	public ResponseData getResponse() {
		return Response;
	}

	/**
	 * 获取Cookie数据
	 */
	public CookieData getCookies() {
		return Cookies;
	}

	/**
	 * URL重定向
	 * 
	 * @param url
	 */
	public void redirect(String url) {
		Dispatcher.redirect(url);
	}

	/**
	 * 默认的成功消息
	 */
	public void success() {
		success(Lang.get("Common.Success"));
	}

	/**
	 * 成功消息
	 */
	public void success(String message) {
		Response.setSuccessMessage(message);
	}

	/**
	 * 失败消息
	 * 
	 * @param message
	 */
	public void fail(String message) {
		Response.setFailedMessage(message);
	}

	/**
	 * 从Request中获取一个JSONArray对象
	 */
	public JSONArray $A(String id) {
		Object o = this.Request.get(id);
		if ((o instanceof JSONArray)) {
			return (JSONArray) o;
		}
		Object oa = this.Request.get(id + "_JsonArray");
		if ((oa instanceof JSONArray)) {
			return (JSONArray) oa;
		}
		JSONArray jo = new JSONArray();
		jo.add(o);
		return jo;
	}

	/**
	 * 从Request中获取一个JSONObject对象
	 */
	public JSONObject $O(String id) {// NO_UCD
		Object o = Request.get(id);
		if (o instanceof JSONObject) {
			return (JSONObject) o;
		}
		return null;
	}

	/**
	 * 从Request中获取一个long值，如果Request中没有，则尝试从Response中获取
	 */
	public long $L(String id) {
		if (Request.containsKey(id)) {
			return Request.getLong(id);
		}
		return Response.getLong(id);
	}

	/**
	 * 从Request中获取一个int值，如果Request中没有，则尝试从Response中获取
	 */
	public int $I(String id) {
		if (Request.containsKey(id)) {
			return Request.getInt(id);
		}
		return Response.getInt(id);
	}

	/**
	 * 从Request中获取一个float值，如果Request中没有，则尝试从Response中获取
	 */
	public float $F(String id) {
		if (Request.containsKey(id)) {
			return Request.getFloat(id);
		}
		return Response.getFloat(id);
	}

	/**
	 * 从Request中获取一个double值，如果Request中没有，则尝试从Response中获取
	 */
	public double $D(String id) {
		if (Request.containsKey(id)) {
			return Request.getDouble(id);
		}
		return Response.getDouble(id);
	}

	/**
	 * 从Request中获取一个boolean值，如果Request中没有，则尝试从Response中获取
	 */
	public boolean $B(String id) {
		if (Request.containsKey(id)) {
			return Request.getBoolean(id);
		}
		return Response.getBoolean(id);
	}
	
	protected Session getSession() {
		return SessionFactory.currentSession();
	}
}
