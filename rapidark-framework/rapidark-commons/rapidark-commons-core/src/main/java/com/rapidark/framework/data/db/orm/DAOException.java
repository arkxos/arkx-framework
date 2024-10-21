package com.rapidark.framework.data.db.orm;

import com.rapidark.framework.core.FrameworkException;

/**
 * DAO操作异常
 * 
 */
public class DAOException extends FrameworkException {

	private static final long serialVersionUID = 1L;

	public DAOException(String message) {
		super(message);
	}

}
