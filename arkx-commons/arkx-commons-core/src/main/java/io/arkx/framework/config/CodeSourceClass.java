package io.arkx.framework.config;

import io.arkx.framework.Config;

/**
 * 配置代码源。
 * 
 */
public class CodeSourceClass implements IApplicationConfigItem {
	public static final String ID = "CodeSource";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "CodeSource's subclass which provider code data";
	}

	public static String getValue() {
		return Config.getValue("App." + ID);
	}

}
