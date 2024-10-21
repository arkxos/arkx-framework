package com.rapidark.framework.core.exception;

import com.rapidark.framework.core.FrameworkException;

/**
 * Bean设置属性值异常
 * 
 */
public class BeanSetPropertyException extends FrameworkException {
	private static final long serialVersionUID = 1L;

	public BeanSetPropertyException(Exception e) {
		super(e);
	}
}
