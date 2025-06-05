package io.arkx.framework.commons.collection;

import java.util.Objects;

/**
 *
 * @author Darkness
 * @date 2013-8-18 上午10:43:45
 * @version V1.0
 */
public class TwoTuple<A, B> {

	public static <A, B> io.arkx.framework.common.collection.TwoTuple<A, B> of(A a, B b) {
		return new io.arkx.framework.common.collection.TwoTuple<>(a, b);
	}

	public final A first;
	public final B second;

	public TwoTuple(A a, B b) {
		first = a;
		second = b;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		io.arkx.framework.common.collection.TwoTuple<?, ?> twoTuple = (io.arkx.framework.common.collection.TwoTuple<?, ?>) o;
		return Objects.equals(first, twoTuple.first) && Objects.equals(second, twoTuple.second);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

}