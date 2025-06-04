package com.arkxos.framework.data.fasttable;

/**
 * @author Darkness
 * @date 2017年7月14日 下午4:09:33
 * @version 1.0
 * @since 1.0 
 */
public class FastTableException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public FastTableException(String message) {
		super(message);
	}
	
	public FastTableException(Throwable throwable) {
		super(throwable);
	}

}
