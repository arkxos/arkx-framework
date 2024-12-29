package com.arkxos.framework.commons.util;

/**
 * 
 * @author Darkness
 * @date 2015年8月29日 下午12:54:25
 * @version V1.0
 * @since infinity 1.0
 */
public class IntegerValue {

	private int value;

	public IntegerValue() {
		this(0);
	}

	public IntegerValue(int value) {
		this.value = value;
	}

	public void add() {
		value++;
	}
	
	public void add(int value) {
		this.value+=value;
	}
	
	public void set(int value) {
		this.value = value;
	}
	
	public int get() {
		return value;
	}
}
