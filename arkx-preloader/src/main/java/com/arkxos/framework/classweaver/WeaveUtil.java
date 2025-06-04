package com.arkxos.framework.classweaver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class WeaveUtil extends ClassLoader {
	public static void invokeStaticMethod(String weaverClass, String targetMethod, Object[] args) throws Exception {
		WeaveUtil loader = new WeaveUtil();
		ClassWeaver weaver = new ClassWeaver(weaverClass);
		ClassNode cnt = weaver.weave();
		if (cnt != null) {
			ClassWriter cw = new ClassWriter(0);
			cnt.accept(cw);
			byte[] code = cw.toByteArray();
			String className = weaver.getTargetClassName();

			Class<?> weavedClass = loader.defineClass(className, code, 0, code.length);
			boolean flag = false;
			Method[] arrayOfMethod;
			int j = (arrayOfMethod = weavedClass.getMethods()).length;
			for (int i = 0; i < j; i++) {
				Method m = arrayOfMethod[i];
				if (((m.getModifiers() & 0x8) != 0) && (m.getName().equals(targetMethod)) && (matchArgs(m.getParameterTypes(), args))) {
					flag = true;
					m.invoke(null, args);
					break;
				}
			}
			if (!flag) {
				throw new RuntimeException("WeaveUtil.invokeStaticMethod() failed:Weaver=" + weaverClass + ",Method=" + targetMethod);
			}
		}
	}

	public static void invokeInstanceMethod(String weaverClass, Object[] constructorArgs, String targetMethod, Object[] args) throws Exception {
		WeaveUtil loader = new WeaveUtil();
		ClassWeaver weaver = new ClassWeaver(weaverClass);
		ClassNode cnt = weaver.weave();
		if (cnt != null) {
			ClassWriter cw = new ClassWriter(0);
			cnt.accept(cw);
			byte[] code = cw.toByteArray();
			String className = weaver.getTargetClassName();

			Class<?> weavedClass = loader.defineClass(className, code, 0, code.length);
			boolean flag = false;
			Object instance = null;
			Constructor[] localObject1 = weavedClass.getConstructors();
			int j = localObject1.length;
			for (int i = 0; i < j; i++) {
				Constructor<?> m = localObject1[i];
				if (matchArgs(m.getParameterTypes(), args)) {
					flag = true;
					instance = m.newInstance(args);
					break;
				}
			}
			if (!flag) {
				throw new RuntimeException("WeaveUtil.invokeInstanceMethod() failed:Weaver=" + weaverClass + ",Constructor not found!");
			}
			Method[] localObject1M = weavedClass.getMethods();
			j = localObject1.length;
			for (int i = 0; i < j; i++) {
				Method m = localObject1M[i];
				if (((m.getModifiers() & 0x8) == 0) && (m.getName().equals(targetMethod)) && (matchArgs(m.getParameterTypes(), args))) {
					flag = true;
					m.invoke(instance, args);
					break;
				}
			}
			if (!flag) {
				throw new RuntimeException("WeaveUtil.invokeInstanceMethod() failed:Weaver=" + weaverClass + ",Method=" + targetMethod);
			}
		}
	}

	private static boolean matchArgs(Class<?>[] params, Object[] args) {
		if ((args == null) || (args.length == 0)) {
			if ((params != null) && (params.length != 0)) {
				return false;
			}
		} else {
			if ((params == null) || (params.length == 0)) {
				return false;
			}
			if (params.length != args.length) {
				return false;
			}
			for (int i = 0; i < args.length; i++) {
				if ((args[i] != null) && (!args.getClass().isAssignableFrom(params[i]))) {
					return false;
				}
			}
		}
		return true;
	}
}
