package com.arkxos.framework.data.db.exception;

/**
 * 修改字段、主键异常
 * 
 */
public class AlterException extends DDLException {

	private static final long serialVersionUID = 1L;

	public AlterException(Exception e) {
		super(e);
	}

}
