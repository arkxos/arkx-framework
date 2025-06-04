package com.arkxos.framework.config;

import com.arkxos.framework.Config;
import io.arkx.framework.commons.util.ObjectUtil;

/**
 * 配置日志管理器的实现类，此类必须实现com.arkxos.framework.utility.ILogManager接口。
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
		return "Class name which implements com.arkxos.framework.utility.log.ILogManager";
	}

	public static String getValue() {
		String v = Config.getValue("App." + ID);
		if (ObjectUtil.isEmpty(v)) {
			v = "log.util.io.arkx.framework.commons.ConsoleLogManager";
		}
		return v;
	}

}
