package org.ark.framework.core.castor;

import io.arkx.framework.commons.util.ObjectUtil;

/**
 * float类型转换器
 *
 * @author Darkness
 * @date 2013-3-26 下午08:14:47
 * @version V1.0
 */
public class FloatCastor extends AbstractInnerCastor {

	private static FloatCastor singleton = new FloatCastor();

	public static FloatCastor getInstance() {
		return singleton;
	}

	public boolean canCast(Class<?> type) {
		return (Float.class == type) || (Float.TYPE == type);
	}

	public Object cast(Object obj, Class<?> type) {
		if (obj == null) {
			return Integer.valueOf(0);
		}
		if ((obj instanceof Number)) {
			return ((Number) obj).floatValue();
		}
		String str = obj.toString();
		if (ObjectUtil.empty(str))
			return Integer.valueOf(0);
		try {
			return Float.valueOf(Float.parseFloat(obj.toString()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return Float.valueOf(0.0F);
	}

}
