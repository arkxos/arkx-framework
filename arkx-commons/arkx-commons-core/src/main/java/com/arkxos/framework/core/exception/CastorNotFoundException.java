package com.arkxos.framework.core.exception;

import com.arkxos.framework.core.FrameworkException;

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
