package io.arkx.framework.data.db.exception;

/**
 * 事务回滚异常
 * 
 */
public class RollbackException extends DatabaseException {

	private static final long serialVersionUID = 1L;

	public RollbackException(Exception e) {
		super(e);
	}

}
