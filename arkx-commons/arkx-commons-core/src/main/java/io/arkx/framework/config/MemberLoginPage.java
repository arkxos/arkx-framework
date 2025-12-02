package io.arkx.framework.config;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.ObjectUtil;

/**
 * 配置前台会员登录页面相对于应用根目录的地址。
 *
 */
public class MemberLoginPage implements IApplicationConfigItem {

	public static final String ID = "MemberLoginPage";

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Member login URL";
	}

	public static String getValue() {
		String v = Config.getValue("App." + ID);
		if (ObjectUtil.empty(v)) {
			v = "member/login";
		}
		return v;
	}

}
