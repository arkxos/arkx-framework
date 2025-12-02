package io.arkx.framework.extend.action;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

/**
 * UI方法调用后执行
 *
 * @author Darkness
 * @date 2012-8-7 下午9:30:31
 * @version V1.0
 */
public abstract class AfterUIMethodInvokeAction implements IExtendAction {
    public static final String ExtendPointID = "io.arkx.framework.AfterUIMethodInvoke";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        execute((String) args[0]);
        return null;
    }

    public abstract void execute(String method) throws ExtendException;
}
