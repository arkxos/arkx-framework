package io.arkx.framework.core.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import io.arkx.framework.core.exception.UIMethodInvokeException;
import io.arkx.framework.cosyui.web.mvc.Dispatcher.DispatchException;

/**
 * UIMethod类定位器
 * 
 */
public class MethodClassLocator implements IMethodLocator {
	protected Class<? extends UIMethod> clazz;

	public MethodClassLocator(Class<? extends UIMethod> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Object execute(Object... args) {
		try {
			UIMethod instance = clazz.newInstance();
			UIMethodBinder.bind(instance, args);
			instance.execute();
		} catch (Exception e) {
			if (e.getCause() != null) {
				if (e.getCause() instanceof DispatchException) {// 需要避免捕获
					throw (DispatchException) e.getCause();
				}
				if (e.getCause() instanceof RuntimeException) {
					throw (RuntimeException) e.getCause();
				}
				throw new UIMethodInvokeException(e.getCause());
			} else {
				throw new UIMethodInvokeException(e);
			}
		}
		return null;
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return clazz.isAnnotationPresent(annotationClass);
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return clazz.getAnnotation(annotationClass);
	}

	@Override
	public String getName() {
		return clazz.getName();
	}

	@Override
	public Method getMethod() {
		return null;
	}
}
