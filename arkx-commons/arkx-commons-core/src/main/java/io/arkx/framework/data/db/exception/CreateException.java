package io.arkx.framework.data.db.exception;

/**
 * 创建字段、索引、数据表异常
 *
 */
public class CreateException extends DDLException {

	private static final long serialVersionUID = 1L;

	public CreateException(Exception e) {
		super(e);
	}

	public CreateException(String message) {
		super(message);
	}

	public CreateException(String message, Throwable cause) {
		super(message, cause);
	}

}
