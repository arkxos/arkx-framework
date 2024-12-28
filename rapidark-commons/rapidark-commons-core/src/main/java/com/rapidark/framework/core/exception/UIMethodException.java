package com.rapidark.framework.core.exception;

import com.rapidark.framework.core.FrameworkException;

/**
 * UIMethod异常
 * 
 */
public class UIMethodException extends FrameworkException {
	private static final long serialVersionUID = 1L;

	public UIMethodException(String message) {
		super(message);
	}

	public UIMethodException(Throwable e) {
		super(e);
	}
}
