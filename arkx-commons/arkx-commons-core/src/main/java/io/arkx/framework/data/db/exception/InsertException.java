package io.arkx.framework.data.db.exception;

/**
 * 插入数据异常
 *
 */
public class InsertException extends DatabaseException {

    private static final long serialVersionUID = 1L;

    public InsertException(Exception e) {
        super(e);
    }

    public InsertException(String message) {
        super(message);
    }

    public InsertException(String message, Throwable cause) {
        super(message, cause);
    }

}
