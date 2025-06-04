package io.arkx.framework.data.db.exception;

/**
 * 设置Statement变量异常
 * 
 */
public class SetParamException extends DatabaseException {

	private static final long serialVersionUID = 1L;

	public SetParamException(Exception e) {
		super(e);
	}

}
