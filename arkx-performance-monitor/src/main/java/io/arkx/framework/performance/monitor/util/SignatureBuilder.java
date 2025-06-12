package io.arkx.framework.performance.monitor.util;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author Nobody
 * @date 2025-06-11 20:52
 * @since 1.0
 */
public class SignatureBuilder {

	public static String build(String className, String methodName) {
		return className + "#" + methodName;
	}

}
