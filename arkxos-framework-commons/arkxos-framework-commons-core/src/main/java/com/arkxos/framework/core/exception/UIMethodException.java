package com.arkxos.framework.core.exception;

import com.arkxos.framework.core.FrameworkException;

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
