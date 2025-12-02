package org.ark.framework.extend.actions;

import java.lang.reflect.Method;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @class org.ark.framework.extend.actions.AfterZActionPrivCheckFailedAction
 *        action权限检查失败后行为
 *
 * @author Darkness
 * @date 2012-8-7 下午9:31:51
 * @version V1.0
 */
public abstract class AfterZActionPrivCheckFailedAction implements IExtendAction {

    public static final String ID = "org.ark.framework.AfterZActionPrivCheckFailedAction";

    public Object execute(Object[] args) throws ExtendException {
        HttpServletRequest request = (HttpServletRequest) args[0];
        HttpServletResponse response = (HttpServletResponse) args[1];
        Method m = (Method) args[2];
        execute(m, request, response);
        return null;
    }

    public abstract void execute(Method paramMethod, HttpServletRequest paramHttpServletRequest,
            HttpServletResponse paramHttpServletResponse);
}
