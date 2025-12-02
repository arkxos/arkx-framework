package io.arkx.framework.commons.collection;

/**
 *
 * @author Darkness
 * @date 2013-8-18 上午10:43:45
 * @version V1.0
 */
public class ComparableTwoTuple<A extends Comparable<A>, B extends Comparable<B>> extends TwoTuple<A, B>
        implements
            Comparable<ComparableTwoTuple<A, B>> {

    public static <A extends Comparable<A>, B extends Comparable<B>> ComparableTwoTuple<A, B> of(A a, B b) {
        return new ComparableTwoTuple<>(a, b);
    }

    public ComparableTwoTuple(A a, B b) {
        super(a, b);
    }

    @Override
    public int compareTo(ComparableTwoTuple<A, B> other) {
        int compareValue = first.compareTo(other.first);
        if (compareValue != 0) {
            return compareValue;
        }
        return second.compareTo(other.second);
    }

}
