package io.arkx.framework.config;

import com.arkxos.framework.Config;

/**
 * 配置默认语言。
 * 
 */
public class DefaultLanguage implements IApplicationConfigItem {
	public static final String ID = "DefaultLanguage";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Backend default language";
	}

	public static String getValue() {
		return Config.getValue("App." + ID);
	}

}
