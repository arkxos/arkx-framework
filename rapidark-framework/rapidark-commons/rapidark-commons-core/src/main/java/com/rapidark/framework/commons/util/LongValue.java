package com.rapidark.framework.commons.util;

/**
 * 
 * @author Darkness
 * @date 2015年8月29日 下午12:54:25
 * @version V1.0
 * @since infinity 1.0
 */
public class LongValue {

	private long value;

	public LongValue() {
		this(0);
	}

	public LongValue(long value) {
		this.value = value;
	}

	public void add() {
		value++;
	}
	
	public void add(long value) {
		this.value+=value;
	}
	
	public void set(long value) {
		this.value = value;
	}
	
	public long get() {
		return value;
	}
}
