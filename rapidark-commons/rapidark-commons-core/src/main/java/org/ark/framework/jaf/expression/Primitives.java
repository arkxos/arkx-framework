package org.ark.framework.jaf.expression;

import com.rapidark.framework.commons.util.NumberUtil;
import com.rapidark.framework.commons.util.ObjectUtil;

/**
 * @class org.ark.framework.jaf.expression.Primitives
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:48:08 
 * @version V1.0
 */
public class Primitives {
	public static double getDouble(Object o) {
		if ((o instanceof Number))
			return ((Number) o).doubleValue();
		if (o != null) {
			try {
				String str = o.toString();
				if (ObjectUtil.empty(str)) {
					return 0.0D;
				}
				return Double.parseDouble(o.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0.0D;
	}

	public static long getLong(Object o) {
		if ((o instanceof Number))
			return ((Number) o).longValue();
		if (o != null) {
			try {
				String str = o.toString();
				if (ObjectUtil.empty(str)) {
					return 0L;
				}
				return Long.parseLong(o.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0L;
	}

	public static Boolean getBoolean(boolean flag) {
		return flag ? Boolean.TRUE : Boolean.FALSE;
	}

	public static boolean getBoolean(Object obj) {
		if (obj == null) {
			return false;
		}
		if ((obj instanceof Number))
			return ((Number) obj).doubleValue() > 0.0D;
		if ((obj instanceof Boolean))
			return ((Boolean) obj).booleanValue();
		if ((obj instanceof String)) {
			if ((obj.equals("")) || (obj.equals("false"))) {
				return false;
			}
			if (NumberUtil.isNumber((String) obj)) {
				return Double.parseDouble((String) obj) > 0.0D;
			}
			return true;
		}
		return false;
	}
}