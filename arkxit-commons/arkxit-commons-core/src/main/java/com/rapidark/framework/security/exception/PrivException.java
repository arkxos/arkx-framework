package com.rapidark.framework.security.exception;

import com.rapidark.framework.core.FrameworkException;
import com.rapidark.framework.extend.ExtendManager;
import com.rapidark.framework.extend.action.AfterPrivCheckFailedAction;

/**
 * 权限异常
 * 
 */
public abstract class PrivException extends FrameworkException {
	private static final long serialVersionUID = 1L;

	public PrivException(String message) {
		super(message);
		ExtendManager.invoke(AfterPrivCheckFailedAction.ID, new Object[] { message });
	}

}
