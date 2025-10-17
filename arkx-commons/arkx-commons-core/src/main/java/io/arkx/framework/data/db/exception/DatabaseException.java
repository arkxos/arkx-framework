package io.arkx.framework.data.db.exception;

import io.arkx.framework.core.FrameworkException;

/**
 * 数据库异常
 * 
 */
public class DatabaseException extends FrameworkException {

	private static final long serialVersionUID = 1L;

	public DatabaseException(Throwable e) {
		super(e);
	}

	public DatabaseException(String message) {
		super(message);
	}

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

}
