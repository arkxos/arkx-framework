package io.arkx.framework.core.castor;

import io.arkx.framework.commons.util.ObjectUtil;

/**
 * 整型转换器
 * 
 */
public class IntCastor extends AbstractCastor {
	private static IntCastor singleton = new IntCastor();

	public static IntCastor getInstance() {
		return singleton;
	}

	private IntCastor() {
	}

	@Override
	public boolean canCast(Class<?> type) {
		return Integer.class == type || int.class == type;
	}

	@Override
	public Object cast(Object obj, Class<?> type) {
		if (obj == null) {
			return 0;
		}
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		} else {
			try {
				String str = obj.toString();
				if (ObjectUtil.empty(str) || str.equals("null") || str.equals("undefined")) {
					return 0;
				}
				return Integer.parseInt(obj.toString());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return 0;
	}

}
