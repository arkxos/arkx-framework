package io.arkx.framework.core.exception;

import io.arkx.framework.core.FrameworkException;

/**
 * Bean异常
 *
 */
public class BeanException extends FrameworkException {
    private static final long serialVersionUID = 1L;

    public BeanException(String message) {
        super(message);
    }
}
