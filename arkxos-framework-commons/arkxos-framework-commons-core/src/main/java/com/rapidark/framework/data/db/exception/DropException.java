package com.rapidark.framework.data.db.exception;

/**
 * 删除字段、索引、键、数据表异常
 * 
 */
public class DropException extends DDLException {

	private static final long serialVersionUID = 1L;

	public DropException(Exception e) {
		super(e);
	}

}
