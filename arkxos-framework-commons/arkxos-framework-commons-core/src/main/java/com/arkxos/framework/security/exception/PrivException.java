package com.arkxos.framework.security.exception;

import com.arkxos.framework.core.FrameworkException;
import com.arkxos.framework.extend.ExtendManager;
import com.arkxos.framework.extend.action.AfterPrivCheckFailedAction;

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
