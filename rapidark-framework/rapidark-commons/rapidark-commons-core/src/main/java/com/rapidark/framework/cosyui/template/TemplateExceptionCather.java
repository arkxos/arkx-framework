package com.rapidark.framework.cosyui.template;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rapidark.framework.commons.util.LogUtil;
import com.rapidark.framework.core.IExceptionCatcher;
import com.rapidark.framework.cosyui.template.exception.TemplateException;

/**
 * 模板异常捕获器
 * 
 */
public class TemplateExceptionCather implements IExceptionCatcher {

	@Override
	public String getExtendItemID() {
		return "com.rapidark.framework.cosyui.template.TemplateExceptionCather";
	}

	@Override
	public String getExtendItemName() {
		return "Default Template Exception Catcher";
	}

	@Override
	public Class<?>[] getTargetExceptionClass() {
		return new Class<?>[] { TemplateException.class };
	}

	@Override
	public void doCatch(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
		LogUtil.error("TemplateException found in " + request.getRequestURL());
		throw e;
	}

}
