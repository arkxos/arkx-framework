package org.ark.framework.jaf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import org.ark.framework.security.PrivCheck;
import org.ark.framework.security.VerifyCheck;

import com.arkxos.framework.Config;
import com.arkxos.framework.Constant;
import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.cosyui.web.ResponseData;
import com.arkxos.framework.extend.ExtendManager;
import com.arkxos.framework.extend.action.AfterUIMethodInvokeAction;
import com.arkxos.framework.extend.action.BeforeUIMethodInvokeAction;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * @class org.ark.framework.MainServlet
 *        框架主控制器，处理前端发送过来的请求，动态调用对应的UIFacade方法
 * 
 * @author Darkness
 * @date 2012-8-5 下午12:21:43
 * @version V1.0
 */
public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

			dealWithHead(request, response);

			String url = request.getParameter(Constant.URL);
			if (("".equals(url)) || ("/".equals(url))) {
				url = "/Index.zhtml";
			}

			String method = request.getParameter(Constant.Method);
			if (StringUtil.isEmpty(method)) {
				LogUtil.warn("Error in Server.sendRequest(),QueryString=" + request.getQueryString() + ",Referer=" + request.getHeader("referer"));
				return;
			}
			
//			CurrentConnection.bindTransactionToThread();

			Method m = Current.prepareMethod(request, response, method, null);

			if (!PrivCheck.check(m, Current.getRequest(), Current.getResponse())) {
				String message = "Priv check failed:method=" + method;
				LogUtil.warn(message);
				Current.getResponse().setFailedMessage(message);
				response.getWriter().write(Current.getResponse().toXML());
				return;
			}

			if (!VerifyCheck.check(m)) {
				String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
				LogUtil.warn(message);
				Current.getResponse().setFailedMessage(message);
				response.getWriter().write(Current.getResponse().toXML());
				return;
			}

			ExtendManager.invoke(BeforeUIMethodInvokeAction.ExtendPointID, new Object[] { method });

			Current.invokeMethod(m, null);

			ExtendManager.invoke(AfterUIMethodInvokeAction.ExtendPointID, new Object[] { method });

//			CurrentConnection.clearTransactionBinding();
			
			response.getWriter().write(Current.getResponse().toXML());
		} catch (Throwable e) {
			e.printStackTrace();
			ResponseData r = new ResponseData();
			r.setFailedMessage(e.getMessage());
			response.getWriter().write(r.toXML());
			
//			try {
//				CurrentConnection.getCurrentThreadConnection().rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//			CurrentConnection.clearTransactionBinding();
		}
	}

	private void dealWithHead(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Pragma", "No-Cache");
		response.setHeader("Cache-Control", "No-Cache");
		response.setDateHeader("Expires", 0L);

		response.setContentType("text/xml");

		if ((Config.ServletMajorVersion == 2) && (Config.ServletMinorVersion == 3))
			response.setContentType("text/xml;charset=utf-8");
		else {
			response.setCharacterEncoding("UTF-8");
		}
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (!"true".equals(request.getParameter(Constant.NoSession))) {
			request.getSession(true);
		}
	}
}