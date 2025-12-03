package io.arkx.framework.security.exception;

/**
 * 用户未登录异常
 *
 */
public class UserNotLoginException extends PrivException {

    private static final long serialVersionUID = 1L;

    public UserNotLoginException(String message) {
        super(message);
    }

}
