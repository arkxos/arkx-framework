package com.arkxos.framework.extend.exception;

import com.arkxos.framework.core.FrameworkException;

/**
 * 创建扩展行为实例异常
 * 
 */
public class CreateExtendActionInstanceException extends FrameworkException {

	private static final long serialVersionUID = 1L;

	public CreateExtendActionInstanceException(String message) {
		super(message);
	}

	public CreateExtendActionInstanceException(Throwable t) {
		super(t);
	}
}
