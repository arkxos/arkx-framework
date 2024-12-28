package com.rapidark.framework.commons.util;

import com.rapidark.framework.core.FrameworkException;

/**
 * RegexParser匹配的过程中发现有固定字符串找不到，则直接抛出此异常以中止后续匹配
 * 
 */
public class RegexMatchFailedException extends FrameworkException {
	private static final long serialVersionUID = 1L;

	public RegexMatchFailedException(String message) {
		super(message);
	}

}
