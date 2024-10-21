package com.rapidark.framework.config;

import com.rapidark.framework.Config;
import com.rapidark.framework.commons.util.LogUtil;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.commons.util.StringUtil;

public class ContainerStartUser implements IApplicationConfigItem {
	public static final String ID = "ContainerStartUser";

	public String getExtendItemID() {
		return ID;
	}

	public String getExtendItemName() {
		return "Container start user";
	}

	public static String getValue() {
		return Config.getValue("App." + ID);
	}

	public static void isMatch() {
		String v = getValue();
		if (StringUtil.isEmpty(v)) {
			return;
		}
		String user = System.getProperty("user.name");
		if (ObjectUtil.equal(user, getValue())) {
			return;
		}
		LogUtil.error("Container start user not match the config value in " + Config.getContextRealPath() + "framework.xml!");
		System.exit(1);
	}
}
