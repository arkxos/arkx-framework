package io.arkx.framework.data.xml;

import io.arkx.framework.core.FrameworkException;

/**
 * XML解析异常
 * 
 */
public class XMLParseException extends FrameworkException {

	private static final long serialVersionUID = 1L;

	public XMLParseException(String message) {
		super(message);
	}

	public XMLParseException(Throwable t) {
		super(t);
	}

}
