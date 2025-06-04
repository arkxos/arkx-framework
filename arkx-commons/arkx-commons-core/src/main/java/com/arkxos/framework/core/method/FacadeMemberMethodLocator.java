package com.arkxos.framework.core.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.arkxos.framework.Current;
import io.arkx.framework.commons.util.ObjectUtil;
import com.arkxos.framework.core.exception.UIMethodException;
import com.arkxos.framework.core.exception.UIMethodInvokeException;
import com.arkxos.framework.cosyui.web.UIFacade;
import com.arkxos.framework.cosyui.web.mvc.Dispatcher.DispatchException;

/**
 * UIFacade中的成员方法定位器
 */
public class FacadeMemberMethodLocator implements IMethodLocator {
	
	private Class<?> clazz;
	private Method m;
	private String[] params;
	
	public FacadeMemberMethodLocator(Class<?> clazz, Method m, String paramsStr) {
		this.clazz = clazz;
		this.m = m;
		if (!UIFacade.class.isAssignableFrom(clazz)) {
			throw new UIMethodException("Method " + m.getName() + " 's declaring class " + m.getDeclaringClass().getName()
					+ " not inherit from UIFacade");
		}
		if (Modifier.isStatic(m.getModifiers())) {
			throw new UIMethodException("Method " + m.getName() + " in declaring class " + m.getDeclaringClass().getName()
					+ " has modifier 'static'");
		}
		if (!Modifier.isPublic(m.getModifiers())) {
			throw new UIMethodException("Method " + m.getName() + " in declaring class " + m.getDeclaringClass().getName()
					+ " should has modifier 'public'");
		}
		if (!ObjectUtil.isEmpty(paramsStr)) {
			this.params = paramsStr.split("\\,");
		}
	}

	@Override
	public Object execute(Object... args) {
		UIFacade ui = createFacadeInstance();
		try {
			return m.invoke(ui, UIMethodBinder.convertArg(m, args, this.params));
		} catch (IllegalArgumentException e) {
			throw new UIMethodInvokeException(e);
		} catch (IllegalAccessException e) {
			throw new UIMethodInvokeException(e);
		} catch (InvocationTargetException e) {
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
	}

	private UIFacade createFacadeInstance() {
		Class<?> c = null;
		c = clazz;//m.getDeclaringClass();
		if (Current.getUIFacade() != null && Current.getUIFacade().getClass() == c) {
			return Current.getUIFacade();
		} else {
			try {
				UIFacade facade = (UIFacade) c.newInstance();
				Current.setUIFacade(facade);
				return facade;
			} catch (Exception e) {
				throw new UIMethodInvokeException(e);
			}
		}
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return m.isAnnotationPresent(annotationClass);
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return m.getAnnotation(annotationClass);
	}

	@Override
	public String getName() {
		return m.getDeclaringClass().getName() + "." + m.getName();
	}
	
	@Override
	public Method getMethod() {
		return m;
	}
		
}
