package com.rapidark.framework.extend.exception;

import com.rapidark.framework.core.FrameworkException;

/**
 * 创建扩展项实例异常
 * 
 */
public class CreateExtendItemInstanceException extends FrameworkException {

	private static final long serialVersionUID = 1L;

	public CreateExtendItemInstanceException(String message) {
		super(message);
	}

	public CreateExtendItemInstanceException(Throwable t) {
		super(t);
	}
}
