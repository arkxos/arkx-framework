package org.ark.framework.extend.actions;

import io.arkx.framework.extend.IExtendAction;

/**
 * @class org.ark.framework.extend.actions.AfterConfigInitAction 配置初始化之后行为
 * @author Darkness
 * @date 2012-8-7 下午9:27:09
 * @version V1.0
 */
public abstract class AfterConfigInitAction implements IExtendAction {

	public static final String ExtendPointID = "org.ark.framework.AfterConfigInit";

	@Override
	public boolean isUsable() {
		// TODO Auto-generated method stub
		return false;
	}

}
