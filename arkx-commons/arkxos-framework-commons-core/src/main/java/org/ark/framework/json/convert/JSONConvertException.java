package org.ark.framework.json.convert;

/**
 * 
 * 
 * @author Darkness
 * @date 2013-3-30 下午03:56:46 
 * @version V1.0
 */
public class JSONConvertException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public JSONConvertException(String message) {
		super(message);
	}

	public JSONConvertException(Throwable t) {
		super(t);
	}

	public JSONConvertException(String message, Throwable cause) {
		super(message, cause);
	}
}