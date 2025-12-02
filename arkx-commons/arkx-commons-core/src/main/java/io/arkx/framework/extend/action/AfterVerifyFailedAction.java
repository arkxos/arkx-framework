package io.arkx.framework.extend.action;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

/**
 * 数据校验失败后执行
 *
 * @author Darkness
 * @date 2012-8-7 下午9:31:14
 * @version V1.0
 */
public abstract class AfterVerifyFailedAction implements IExtendAction {

	public static final String ID = "io.arkx.framework.AfterPrivCheckFailedAction";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		String methodName = (String) args[0];
		String k = (String) args[1];
		String v = (String) args[2];
		String rule = (String) args[3];
		execute(methodName, k, v, rule);
		return null;
	}

	public abstract void execute(String methodName, String k, String v, String rule);

}
