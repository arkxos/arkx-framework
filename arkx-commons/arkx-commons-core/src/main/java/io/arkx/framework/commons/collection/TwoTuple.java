package io.arkx.framework.commons.collection;

/**
 * 
 * @author Darkness
 * @date 2013-8-18 上午10:43:45
 * @version V1.0
 */
public class TwoTuple<A, B> {
	
	public static <A, B> TwoTuple<A, B> of(A a, B b) {
		return new TwoTuple<>(a, b);
	}
	
	public final A first;
	public final B second;

	public TwoTuple(A a, B b) {
		first = a;
		second = b;
	}

	public String toString() {
		return "(" + first + ", " + second + ")";
	}

}
