package io.arkx.framework.cosyui.web.mvc;

import java.io.IOException;

import io.arkx.framework.extend.IExtendItem;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * URL处理者，负责执行某种类型URL
 *
 */
public interface IURLHandler extends IExtendItem {
    /**
     * 指定URL是否由本类负责处理
     */
    public boolean match(String url);

    /**
     * 执行URL
     *
     * @return 为true则中止后续执行输出响应到浏览器，为flase则继续执行其他Filter
     */
    public boolean handle(String url, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException;

    /**
     * 初始化URL处理者
     */
    public void init();

    /**
     * 顺序位，顺序位小的先执行，有一个execute返回结果为true则后续的IURLProcessor不再执行
     */
    public int getOrder();

    /**
     * 销毁URL处理者
     */
    public void destroy();
}
