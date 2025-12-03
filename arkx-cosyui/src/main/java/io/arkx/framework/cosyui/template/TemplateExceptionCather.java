package io.arkx.framework.cosyui.template;

import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.core.IExceptionCatcher;
import io.arkx.framework.cosyui.template.exception.TemplateException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 模板异常捕获器
 *
 */
public class TemplateExceptionCather implements IExceptionCatcher {

    @Override
    public String getExtendItemID() {
        return "template.io.arkx.framework.cosyui.TemplateExceptionCather";
    }

    @Override
    public String getExtendItemName() {
        return "Default Template Exception Catcher";
    }

    @Override
    public Class<?>[] getTargetExceptionClass() {
        return new Class<?>[]{TemplateException.class};
    }

    @Override
    public void doCatch(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
        LogUtil.error("TemplateException found in " + request.getRequestURL());
        throw e;
    }

}
