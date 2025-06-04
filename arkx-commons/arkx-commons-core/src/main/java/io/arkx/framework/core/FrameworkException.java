package io.arkx.framework.core;

/**
 * ARK框架异常
 * @author Darkness
 * @date 2014-9-19 下午4:59:03
 * @version V1.0
 */
public class FrameworkException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FrameworkException(String message) {
		super(message);
	}

	public FrameworkException(Throwable t) {
		super(t);
	}

	public FrameworkException(String message, Throwable cause) {
		super(message, cause);
	}

}
