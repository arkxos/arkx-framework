package com.rapidark.framework.extend;

/**
 * 扩展异常类
 * @author Darkness
 * @date 2012-8-7 下午9:24:35
 * @version V1.0
 */
public class ExtendException extends Exception {
	private static final long serialVersionUID = 1L;

	private String message;

	public ExtendException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
