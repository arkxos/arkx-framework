package io.arkx.framework.extend.action;

import io.arkx.framework.extend.ExtendException;
import io.arkx.framework.extend.IExtendAction;

/**
 * 在所有插件初始化完成后执行此动作
 *
 * @author Darkness
 * @date 2012-8-7 下午9:28:47
 * @version V1.0
 */
public abstract class AfterAllPluginStartedAction implements IExtendAction {

	public static final String ExtendPointID = "io.arkx.framework.AfterAllPluginStarted";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		execute();
		return null;
	}

	public abstract void execute();

}
