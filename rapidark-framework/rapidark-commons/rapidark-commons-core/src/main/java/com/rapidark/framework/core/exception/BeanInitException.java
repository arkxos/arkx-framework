package com.rapidark.framework.core.exception;

import com.rapidark.framework.core.FrameworkException;

/**
 * Bean初始化异常
 * 
 */
public class BeanInitException extends FrameworkException {
	private static final long serialVersionUID = 1L;

	public BeanInitException(String message) {
		super(message);
	}
}
