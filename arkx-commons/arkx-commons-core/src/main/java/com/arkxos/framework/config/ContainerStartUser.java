package com.arkxos.framework.config;

import com.arkxos.framework.Config;
import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;

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
