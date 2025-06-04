package com.arkxos.framework.config;

import com.arkxos.framework.Config;

/**
 * 配置代码源。
 * 
 */
public class CacheSynchronizerClass implements IApplicationConfigItem {
	public static final String ID = "CacheSynchronizerClass";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Class name which implements io.arkx.framework.cache.ICacheSynchronizer";
	}

	public static String getValue() {
		return Config.getValue("App." + ID);
	}

}
