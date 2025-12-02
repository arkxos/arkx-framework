package io.arkx.framework.cosyui.template.exception;

import io.arkx.framework.core.FrameworkException;

/**
 * 模板异常
 *
 */
public class TemplateException extends FrameworkException {

	private static final long serialVersionUID = 1L;

	public TemplateException(String message) {
		super(message);
	}

	public TemplateException(Throwable t) {
		super(t);
	}

	public TemplateException(String message, Throwable cause) {
		super(message, cause);
	}

}
