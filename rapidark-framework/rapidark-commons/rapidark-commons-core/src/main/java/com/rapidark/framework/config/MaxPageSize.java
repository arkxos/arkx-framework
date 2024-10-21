package com.rapidark.framework.config;

import com.rapidark.framework.Config;
import com.rapidark.framework.commons.util.NumberUtil;

/**
 * 每页记录数的最大值，默认1000。<br>
 * 
 */
public class MaxPageSize implements IApplicationConfigItem {
	public static final String ID = "MaxPageSize";
	public static final int DEFAULT = 1_0000;
	private static int max = -1;

	@Override
	public String getExtendItemID() {
		return ID;
	}

	@Override
	public String getExtendItemName() {
		return "Maxiumn page size";
	}

	public static int getValue() {
		if (max < 0) {
			String str = Config.getValue("App." + ID);
			if (NumberUtil.isInt(str)) {
				max = Integer.parseInt(str);
			} else {
				max = DEFAULT;
			}
		}
		return max;
	}
}
