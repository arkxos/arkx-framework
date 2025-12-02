package io.arkx.framework.extend.action;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

/**
 * 权限检查失败后行为
 *
 * @author Darkness
 * @date 2012-8-7 下午9:29:24
 * @version V1.0
 */
public abstract class AfterPrivCheckFailedAction implements IExtendAction {
    public static final String ID = "io.arkx.framework.AfterPrivCheckFailedAction";

    @Override
    public Object execute(Object[] args) throws ExtendException {
        String message = (String) args[0];
        execute(message);
        return null;
    }

    public abstract void execute(String message);

}
