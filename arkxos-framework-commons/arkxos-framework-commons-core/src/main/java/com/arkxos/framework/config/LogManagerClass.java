package com.arkxos.framework.config;

import com.arkxos.framework.commons.util.ObjectUtil;
import com.rapidark.framework.Config;

/**
 * 配置日志管理器的实现类，此类必须实现com.rapidark.framework.utility.ILogManager接口。
 * 
 */
public class LogManagerClass implements IApplicationConfigItem {
	
	public static final String ID = "LogManager";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Class name which implements com.rapidark.framework.utility.log.ILogManager";
	}

	public static String getValue() {
		String v = Config.getValue("App." + ID);
		if (ObjectUtil.isEmpty(v)) {
			v = "com.rapidark.framework.commons.util.log.ConsoleLogManager";
		}
		return v;
	}

}
