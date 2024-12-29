package com.arkxos.framework.cosyui.template;

import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.core.IExceptionCatcher;
import com.arkxos.framework.cosyui.template.exception.TemplateException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
