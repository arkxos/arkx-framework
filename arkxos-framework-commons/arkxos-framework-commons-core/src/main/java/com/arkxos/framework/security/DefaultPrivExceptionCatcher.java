package com.arkxos.framework.security;

import java.io.IOException;

import com.arkxos.framework.Config;
import com.arkxos.framework.Constant;
import com.arkxos.framework.Current;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.config.MemberLoginPage;
import com.arkxos.framework.core.IExceptionCatcher;
import com.arkxos.framework.cosyui.web.ResponseData;
import com.arkxos.framework.cosyui.web.mvc.handler.AjaxHandler;
import com.arkxos.framework.security.exception.MemberNotLoginException;
import com.arkxos.framework.security.exception.PrivException;
import com.arkxos.framework.security.exception.UserNotLoginException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 权限异常捕获器，捕获权限异常并决定跳转到哪个页面
 * 
 */
public class DefaultPrivExceptionCatcher implements IExceptionCatcher {
	public static final String ID = "com.arkxos.framework.security.DefaultPrivExceptionCatcher";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Default PrivException Catcher";
	}

	@Override
	public Class<?>[] getTargetExceptionClass() {
		return new Class<?>[] { PrivException.class };
	}

	@Override
	public void doCatch(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (e instanceof UserNotLoginException) {
				String redirectURL = Config.getContextPath() + Config.getLoginPage();
				if (Current.getURLHandler() instanceof AjaxHandler) {
					ResponseData r = new ResponseData();
					r.put(Constant.ResponseScriptAttr, "window.location.href='" + redirectURL + "';");
					response.getWriter().write(r.toXML());
				} else {
					response.sendRedirect(redirectURL);
				}
			} else if ((e instanceof MemberNotLoginException)) {
				StringBuilder redirectURL = new StringBuilder();
				String url = MemberLoginPage.getValue();
				if ((!url.startsWith("http://")) && (!url.startsWith("https://"))) {
					redirectURL.append(Config.getContextPath());
				}
				redirectURL.append(url);
				String queryString = request.getQueryString();
				if (StringUtil.isNotEmpty(queryString)) {
					if (redirectURL.indexOf("?") > 0) {
						redirectURL.append('&');
					} else {
						redirectURL.append('?');
					}
					redirectURL.append(queryString);
				}
				if ((Current.getURLHandler() instanceof AjaxHandler)) {
					ResponseData r = new ResponseData();
					r.put("_ARK_SCRIPT", "window.location.href='" + redirectURL.toString() + "';");
					response.getWriter().write(r.toXML());
				} else {
					if (response.isCommitted()) {
						return;
					}
					response.sendRedirect(redirectURL.toString());
				}
			} else {
				String redirectURL = Config.getContextPath() + "framework/noPrivilege.zhtml?url=";
				if (Current.getURLHandler() instanceof AjaxHandler) {
					String method = request.getParameter(Constant.Method);
					if (ObjectUtil.notEmpty(method)) {
						redirectURL += "ajax!" + method;
					}
					if (ObjectUtil.notEmpty(request.getQueryString())) {
						redirectURL += "&" + request.getQueryString();
					}
					ResponseData r = new ResponseData();
					r.put(Constant.ResponseScriptAttr, "window.location.href='" + redirectURL + "';");
					response.getWriter().write(r.toXML());
				} else {
					redirectURL += request.getRequestURI();
					if (ObjectUtil.notEmpty(request.getQueryString())) {
						redirectURL += "&" + request.getQueryString();
					}
					response.sendRedirect(redirectURL);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
