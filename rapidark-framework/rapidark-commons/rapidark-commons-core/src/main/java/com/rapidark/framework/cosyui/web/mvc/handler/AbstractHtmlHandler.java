package com.rapidark.framework.cosyui.web.mvc.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rapidark.framework.cosyui.web.mvc.IURLHandler;

/**
 * Html处理者虚拟类，输出html的处理者可以继承本类
 * 
 */
public abstract class AbstractHtmlHandler implements IURLHandler {

	@Override
	public boolean handle(String url, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setHeader("Pragma", "No-Cache");
		response.setHeader("Cache-Control", "No-Cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("text/html");
		return execute(url, request, response);
	}

	public abstract boolean execute(String url, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException;

	@Override
	public void init() {
	}

	@Override
	public void destroy() {
	}
}
