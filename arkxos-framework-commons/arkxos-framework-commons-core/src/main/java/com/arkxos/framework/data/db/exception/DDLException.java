package com.arkxos.framework.data.db.exception;

/**
 * DLL执行异常
 * 
 */
public abstract class DDLException extends DatabaseException {

	private static final long serialVersionUID = 1L;

	public DDLException(Exception e) {
		super(e);
	}

}
