package com.arkxos.framework.data.db.exception;

/**
 * 设置事务提交模式异常
 * 
 */
public class SetAutoCommitException extends DatabaseException {

	private static final long serialVersionUID = 1L;

	public SetAutoCommitException(Exception e) {
		super(e);
	}

}
