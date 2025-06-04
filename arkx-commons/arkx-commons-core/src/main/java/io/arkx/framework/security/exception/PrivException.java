package io.arkx.framework.security.exception;

import io.arkx.framework.core.FrameworkException;
import io.arkx.framework.extend.ExtendManager;
import io.arkx.framework.extend.action.AfterPrivCheckFailedAction;

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
