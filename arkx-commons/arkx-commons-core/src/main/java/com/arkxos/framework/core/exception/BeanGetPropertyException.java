package com.arkxos.framework.core.exception;

import com.arkxos.framework.core.FrameworkException;

/**
 * Bean获取属性异常
 * 
 */
public class BeanGetPropertyException extends FrameworkException {
	private static final long serialVersionUID = 1L;

	public BeanGetPropertyException(Exception e) {
		super(e);
	}
}
