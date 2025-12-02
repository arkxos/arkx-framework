package org.ark.framework.core.castor;

/**
 * 没找到合适的转换器异常
 *
 * @author Darkness
 * @date 2013-3-25 下午08:38:54
 * @version V1.0
 */
public class CastorNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CastorNotFoundException(String message) {
        super(message);
    }

    public CastorNotFoundException(Exception e) {
        super(e);
    }
}
