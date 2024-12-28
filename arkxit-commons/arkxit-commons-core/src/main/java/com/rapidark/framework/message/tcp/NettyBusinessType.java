package com.rapidark.framework.message.tcp;

/**
 * 
 * @author Darkness
 * @date 2017年4月11日 下午3:38:08
 * @version 1.0
 * @since 1.0
 */
public enum NettyBusinessType {

	LOGIN(0), 
	HEARTBEAT(1); 

	private int value;

	private NettyBusinessType(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}
