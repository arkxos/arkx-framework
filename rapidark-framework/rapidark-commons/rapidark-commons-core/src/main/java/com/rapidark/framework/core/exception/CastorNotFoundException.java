package com.rapidark.framework.core.exception;

import com.rapidark.framework.core.FrameworkException;

/**
 * 类型转换器未找到异常
 * 
 */
public class CastorNotFoundException extends FrameworkException {
	private static final long serialVersionUID = 1L;

	public CastorNotFoundException(String message) {
		super(message);
	}
}
