package com.arkxos.framework.config;

import com.arkxos.framework.Config;

/**
 * 配置哪些包不进行注解扫描。
 * 
 */
public class ExcludeClassScan implements IApplicationConfigItem {
	public static final String ID = "ExcludeClassScan";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Class or package exclude in annotation scanning";
	}

	public static String getValue() {
		return Config.getValue("App." + ID);
	}

}
