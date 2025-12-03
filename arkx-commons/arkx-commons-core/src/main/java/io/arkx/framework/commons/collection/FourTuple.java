package io.arkx.framework.commons.collection;

/**
 * @author Darkness
 * @date 2013-8-18 上午10:44:45
 * @version V1.0
 */
public class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {

    public final D fourth;

    public FourTuple(A a, B b, C c, D d) {
        super(a, b, c);
        fourth = d;
    }

    public String toString() {
        return "(" + first + ", " + second + ", " + third + ", " + fourth + ")";
    }

}
