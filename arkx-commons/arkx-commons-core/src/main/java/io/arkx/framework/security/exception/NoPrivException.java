package io.arkx.framework.security.exception;

/**
 * 未拥有权限项异常
 *
 */
public class NoPrivException extends PrivException {
    private static final long serialVersionUID = 1L;

    public NoPrivException(String message) {
        super(message);
    }
}
