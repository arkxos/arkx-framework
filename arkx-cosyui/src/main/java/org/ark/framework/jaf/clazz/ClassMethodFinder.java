package org.ark.framework.jaf.clazz;

import java.lang.reflect.Method;

/**
 * @author Darkness
 * @date 2013-3-22 下午08:37:38
 * @version V1.0
 */
public class ClassMethodFinder {

	public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] types)
			throws SecurityException, NoSuchMethodException {

		Method result = null;
		try {
			result = clazz.getMethod(methodName, types);
		}
		catch (Exception e) {
			// ingore
		}
		if (result != null) {
			return result;
		}

		Method[] ms = clazz.getMethods();
		for (Method method : ms) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}

		throw new NoSuchMethodException("class " + clazz.getName() + " no such method " + methodName);
	}

}
