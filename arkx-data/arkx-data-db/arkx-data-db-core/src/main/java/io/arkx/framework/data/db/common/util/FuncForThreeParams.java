package io.arkx.framework.data.db.common.util;

@FunctionalInterface
public interface FuncForThreeParams<T1, T2, R> {

    R apply(T1 t1, T2 t2);

}
