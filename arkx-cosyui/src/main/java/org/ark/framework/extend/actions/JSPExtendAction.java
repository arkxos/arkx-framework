package org.ark.framework.extend.actions;

import java.io.IOException;

import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.zhtml.ZhtmlIncludeResponseWrapper;
import org.ark.framework.jaf.zhtml.ZhtmlManager;

import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.cosyui.web.RequestData;
import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

/**
 * @class org.ark.framework.extend.actions.JSPExtendAction jsp扩展行为
 * @author Darkness
 * @date 2012-8-7 下午9:34:19
 * @version V1.0
 */
public abstract class JSPExtendAction implements IExtendAction {

    public Object execute(Object[] args) throws ExtendException {

        PageContext pageContext = (PageContext) args[0];
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        RequestData r = Current.initRequest(request);
        JSPContext context = new JSPContext(r);
        execute(context);
        if (!ObjectUtil.empty(context.getOut())) {
            try {
                pageContext.getOut().print(context.getOut());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (context.getIncludes().size() > 0) {
            for (String file : context.getIncludes()) {
                try {
                    ZhtmlIncludeResponseWrapper responseWraper = new ZhtmlIncludeResponseWrapper(response,
                            pageContext.getOut());
                    if (!ZhtmlManager.execute(file, request, responseWraper, pageContext.getServletContext())) {
                        if (!file.startsWith("/")) {
                            file = "/" + file;
                        }
                        pageContext.include(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ExtendException(e.getMessage());
                }
            }
        }
        return null;
    }

    public abstract void execute(JSPContext context) throws ExtendException;

    @Override
    public boolean isUsable() {
        // TODO Auto-generated method stub
        return false;
    }

}
