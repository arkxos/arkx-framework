package com.arkxos.framework.commons.collection;

/**
 * 
 * @author Darkness
 * @date 2013-8-18 上午10:44:45
 * @version V1.0
 */
public class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
	public final C third;

	public ThreeTuple(A a, B b, C c) {
		super(a, b);
		third = c;
	}

	public String toString() {
		return "(" + first + ", " + second + ", " + third + ")";
	}
}

