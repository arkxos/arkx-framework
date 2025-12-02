package io.arkx.framework.data.db.exception;

/**
 * DLL执行异常
 *
 */
public abstract class DDLException extends DatabaseException {

    private static final long serialVersionUID = 1L;

    public DDLException(Exception e) {
        super(e);
    }

    public DDLException(String message) {
        super(message);
    }

    public DDLException(String message, Throwable cause) {
        super(message, cause);
    }

}
