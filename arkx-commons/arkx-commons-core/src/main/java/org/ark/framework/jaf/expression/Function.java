package org.ark.framework.jaf.expression;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import com.arkxos.framework.commons.util.DateUtil;
import com.arkxos.framework.commons.util.StringUtil;


/**
 * @class org.ark.framework.jaf.expression.Function
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:47:26 
 * @version V1.0
 */
public class Function {
	public static Object invoke(Object obj, String method, ArrayList<?> args) {
		if (obj == null) {
			throw new RuntimeException("占位符值为空，调用方法" + method + "失败!");
		}
		if (method.equals("getTime")) {
			if (((obj instanceof Date)) && (args.size() == 0))
				return new Long(((Date) obj).getTime());
			if (((obj instanceof String)) && (DateUtil.isDateTime(obj.toString()))) {
				Date date = DateUtil.parseDateTime(obj.toString());
				return new Long(date.getTime());
			}
		}
		if ((obj instanceof String)) {
			String str = (String) obj;
			if (method.equals("charAt")) {
				int index = ((Number) args.get(0)).intValue();
				return str.substring(index, index + 1);
			}
			if (method.equals("endsWith")) {
				return new Boolean(str.endsWith((String) args.get(0)));
			}
			if (method.equals("startsWith")) {
				return new Boolean(str.startsWith((String) args.get(0)));
			}
			if (method.equals("equalsIgnoreCase")) {
				return new Boolean(str.equalsIgnoreCase((String) args.get(0)));
			}
			if (method.equals("matches")) {
				return new Boolean(str.matches((String) args.get(0)));
			}
			if (method.equals("toUpperCase")) {
				return str.toUpperCase();
			}
			if (method.equals("toLowerCase")) {
				return str.toLowerCase();
			}
			if (method.equals("substring")) {
				if (args.size() == 1) {
					return str.substring(((Number) args.get(0)).intValue());
				}
				if (args.size() == 2) {
					return str.substring(((Number) args.get(0)).intValue(), ((Number) args.get(1)).intValue());
				}
			}
			if (method.equals("indexOf")) {
				if ((args.size() == 1) && ((args.get(0) instanceof String))) {
					return new Integer(str.indexOf((String) args.get(0)));
				}

				if (args.size() == 2) {
					return new Integer(str.indexOf((String) args.get(0), ((Number) args.get(1)).intValue()));
				}
			}
			if (method.equals("lastIndexOf")) {
				if ((args.size() == 1) && ((args.get(0) instanceof String))) {
					return new Integer(str.lastIndexOf((String) args.get(0)));
				}

				if (args.size() == 2) {
					return new Integer(str.lastIndexOf((String) args.get(0), ((Number) args.get(1)).intValue()));
				}
			}
			if (method.equals("replace")) {
				return StringUtil.replaceEx(str, (String) args.get(0), (String) args.get(1));
			}
			if (method.equals("replaceAll")) {
				return str.replaceAll((String) args.get(0), (String) args.get(1));
			}
			if (method.equals("split")) {
				return str.split((String) args.get(0), ((Number) args.get(1)).intValue());
			}
		}
		try {
			Method m = findMethod(obj, method, args);
			Object[] arr = new Object[args.size()];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = args.get(i);
			}
			return m.invoke(obj, arr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Method findMethod(Object obj, String method, ArrayList<?> args) throws SecurityException, NoSuchMethodException {
		Class[] arr = new Class[args.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = args.get(i).getClass();
		}
		return obj.getClass().getDeclaredMethod(method, arr);
	}
}