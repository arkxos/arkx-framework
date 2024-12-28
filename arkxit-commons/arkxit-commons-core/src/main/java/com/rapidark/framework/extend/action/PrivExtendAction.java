package com.rapidark.framework.extend.action;

import com.rapidark.framework.extend.ExtendException;
import com.rapidark.framework.extend.IExtendAction;

/**
 * 权限检查扩展行为虚拟类。<br>
 * 各个插件通过注册权限检查扩展行为实现自定义的权限检查逻辑。
 * 
 * @author Darkness
 * @date 2012-8-7 下午9:34:45
 * @version V1.0
 * 
 */
public abstract class PrivExtendAction implements IExtendAction {
	public static String ExtendPointID = "com.rapidark.framework.PrivCheck";

	@Override
	public Object execute(Object[] args) throws ExtendException {
		return getPrivFlag((String) args[0]);
	}

	public abstract int getPrivFlag(String priv) throws ExtendException;
}
