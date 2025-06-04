package org.ark.framework.security;

import java.lang.reflect.Method;

import org.ark.framework.jaf.Current;

import io.arkx.framework.annotation.Verify;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.extend.ExtendManager;


/**
 * @class org.ark.framework.security.VerifyCheck
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:25:45 
 * @version V1.0
 */
public class VerifyCheck {
	public static boolean check(Method m) {
		if (m == null) {
			return false;
		}

		boolean flag = m.isAnnotationPresent(Verify.class);
		if (flag) {
			Verify v = (Verify) m.getAnnotation(Verify.class);
			boolean ignoreAll = v.ignoreAll();
			if (ignoreAll) {
				return true;
			}
			String nocheck = v.ignoredKeys();
			Mapx map = new Mapx();
			String[] rules = v.value();
			for (String r : rules) {
				int i = r.indexOf("=");
				if (i < 0) {
					continue;
				}
				String name = r.substring(0, i);
				String value = r.substring(i + 1);
				map.put(name, value);
			}
			return check(m, Current.getRequest(), nocheck, map);
		}
		return check(m, Current.getRequest(), null, new Mapx());
	}

	public static boolean check(Mapx<String, Object> data, String nocheck, Mapx<String, String> rules) {
		return check(null, data, nocheck, rules);
	}

	public static boolean check(Method m, Mapx<String, Object> data, String nocheck, Mapx<String, String> rules) {
		String nocheck2 = "," + nocheck + ",";
		for (String k : data.keySet()) {
			if (!(data.get(k) instanceof String)) {
				continue;
			}
			String v = data.getString(k);
			if ((ObjectUtil.empty(v)) || (nocheck2.indexOf(k) > 0)) {
				continue;
			}
			if (k.startsWith("_ARK_")) {
				continue;
			}
			if (rules.containsKey(k)) {
				VerifyRule verify = new VerifyRule((String) rules.get(k));
				if (!verify.verify(v)) {
					log(m, k, v, (String) rules.get(k));
					return false;
				}
			} else {
				v = StringUtil.htmlTagEncode(v);

				if (v.indexOf("'") >= 0) {
					v = v.replaceAll("'", "&#39;");
				}
				if (v.indexOf("\"") >= 0) {
					v = v.replaceAll("\"", "&quot;");
				}
				data.put(k, v);
			}
		}
		return true;
	}

	private static void log(Method m, String k, String v, String rule) {
		String methodName = m.getDeclaringClass().getName() + "." + m.getName();

		ExtendManager.invoke("org.ark.framework.AfterPrivCheckFailedAction", new Object[] { methodName, k, v, rule });
	}
}