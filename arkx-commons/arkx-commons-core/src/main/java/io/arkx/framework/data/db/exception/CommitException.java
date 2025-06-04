package io.arkx.framework.data.db.exception;

/**
 * 事务提交异常
 * 
 */
public class CommitException extends DatabaseException {

	private static final long serialVersionUID = 1L;

	public CommitException(Exception e) {
		super(e);
	}

}
