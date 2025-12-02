package io.arkx.framework.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 16:12
 * @since 1.0
 */
/**
 * 自定义的ObjectInputStream，使用当前线程的上下文类加载器来解析类
 */
public class ContextClassLoaderObjectInputStream extends ObjectInputStream {

	public ContextClassLoaderObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	@Override
	protected Class<?> resolveClass(java.io.ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		String name = desc.getName();
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			return Class.forName(name, true, cl);
		}
		catch (ClassNotFoundException ex) {
			// 如果上下文类加载器找不到类，则回退到默认行为
			return super.resolveClass(desc);
		}
	}

}
