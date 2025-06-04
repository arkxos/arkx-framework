package io.arkx.framework.config;

import io.arkx.framework.Config;

/**
 * 是否统一设置http响应的字符集。
 * 值为true则开启，为false时关闭。
 * 
 */
public class SetResponseEncoding implements IApplicationConfigItem {
	public static final String ID = "SetResponseEncoding";
	public static final String KEY = "App." + ID;

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Set response encoding switch";
	}

	public static boolean getValue() {
		return !"false".equals(Config.getValue(KEY));
	}

}
