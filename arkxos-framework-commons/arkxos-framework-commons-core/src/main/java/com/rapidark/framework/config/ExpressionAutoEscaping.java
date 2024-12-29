package com.rapidark.framework.config;

import com.rapidark.framework.Config;
import com.rapidark.framework.commons.util.Primitives;

/**
 * 转义符是否自动转义，默认为false<br>
 * 
 */
public class ExpressionAutoEscaping implements IApplicationConfigItem {
	public static final String ID = "ExpressionAutoEscaping";
	public static final boolean DEFAULT = false;
	private static boolean loaded = false;
	private static boolean value = DEFAULT;

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Expression value auto escaping";
	}

	public static boolean getValue() {
		if (!loaded) {
			String str = Config.getValue("App." + ID);
			value = Primitives.getBoolean(str);
			loaded = true;
		}
		return value;
	}
}
