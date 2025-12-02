package io.arkx.framework.cosyui.web.mvc.handler;

import java.io.IOException;

import io.arkx.framework.cosyui.web.mvc.IURLHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Html处理者虚拟类，输出html的处理者可以继承本类
 *
 */
public abstract class AbstractHtmlHandler implements IURLHandler {

    @Override
    public boolean handle(String url, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
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
