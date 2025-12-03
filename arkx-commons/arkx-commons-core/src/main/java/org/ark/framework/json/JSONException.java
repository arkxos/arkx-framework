package org.ark.framework.json;

/**
 * @author Darkness
 * @date 2013-3-30 下午03:56:12
 * @version V1.0
 */
public class JSONException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JSONException(String message) {
        super(message);
    }

    public JSONException(Throwable t) {
        super(t);
    }

    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

}
