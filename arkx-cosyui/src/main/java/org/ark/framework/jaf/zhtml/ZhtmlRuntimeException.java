package org.ark.framework.jaf.zhtml;

/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlRuntimeException
 *
 * @author Darkness
 * @date 2013-1-31 下午12:55:51
 * @version V1.0
 */
public class ZhtmlRuntimeException extends Exception {
    private static final long serialVersionUID = 1L;

    public ZhtmlRuntimeException(String message) {
        super(message);
    }

    public ZhtmlRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
