package com.arkxos.framework.data.db.exception;

/**
 * 数据库连接关闭异常
 * 
 */
public class CloseException extends DatabaseException {

	private static final long serialVersionUID = 1L;

	public CloseException(Exception e) {
		super(e);
	}

}
