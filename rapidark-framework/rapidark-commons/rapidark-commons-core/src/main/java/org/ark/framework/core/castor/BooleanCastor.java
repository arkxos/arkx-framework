package org.ark.framework.core.castor;

import com.rapidark.framework.commons.util.NumberUtil;

/**
 * boolean转换器
 * 
 * @author Darkness
 * @date 2013-3-26 下午07:39:21 
 * @version V1.0
 */
public class BooleanCastor extends AbstractInnerCastor {

	private static BooleanCastor singleton = new BooleanCastor();

	public static BooleanCastor getInstance() {
		return singleton;
	}

	@Override
	public boolean canCast(Class<?> type) {
		return (Boolean.class == type) || (Boolean.TYPE == type);
	}

	@Override
	public Object cast(Object obj, Class<?> type) {
		if (obj == null) {
			return false;
		}

		if ((obj instanceof Number)) {
			if (((Number) obj).doubleValue() > 0.0D)
				return true;
			return false;
		}

		if ((obj instanceof Boolean))
			return ((Boolean) obj).booleanValue();

		if ((obj instanceof String)) {
			if ((obj.equals("")) || (obj.equals("false")) || (obj.equals("null"))) {
				return false;
			}
			if (NumberUtil.isNumber((String) obj)) {
				if (Double.parseDouble((String) obj) > 0.0D)
					return true;
				return false;
			}
			return true;
		}

		return false;
	}
}