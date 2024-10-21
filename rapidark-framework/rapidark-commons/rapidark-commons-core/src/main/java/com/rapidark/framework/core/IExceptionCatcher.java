package com.rapidark.framework.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rapidark.framework.extend.IExtendItem;

/**
 * Runtime异常捕获者接口
 * 
 */
public interface IExceptionCatcher extends IExtendItem {
	/**
	 * @return 要捕获的目标异常class
	 */
	Class<?>[] getTargetExceptionClass();

	/**
	 * 执行捕获动作，根据异常信息调用response中的各种方法
	 * 
	 * @param e 要捕获的异常
	 * @param request http请求
	 * @param response http响应
	 */
	void doCatch(RuntimeException e, HttpServletRequest request, HttpServletResponse response);
}
