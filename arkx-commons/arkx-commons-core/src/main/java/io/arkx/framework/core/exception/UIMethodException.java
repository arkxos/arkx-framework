package io.arkx.framework.core.exception;

import io.arkx.framework.core.FrameworkException;

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
