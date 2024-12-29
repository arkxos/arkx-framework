package com.rapidark.framework.data.db;

import com.rapidark.framework.data.db.exception.DatabaseException;

/**
 * 查询数据异常
 * 
 */
public class QueryException extends DatabaseException {
	private static final long serialVersionUID = 1L;

	public QueryException(String message) {
		super(message);
	}

	public QueryException(Throwable e) {
		super(e);
	}

}
