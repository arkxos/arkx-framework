package io.arkx.framework.data.db.exception;

/**
 * 删除数据异常
 * 
 */
public class DeleteException extends DatabaseException {

	private static final long serialVersionUID = 1L;

	public DeleteException(Exception e) {
		super(e);
	}

	public DeleteException(String message) {
		super(message);
	}

}
