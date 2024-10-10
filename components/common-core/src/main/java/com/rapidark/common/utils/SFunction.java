package com.rapidark.common.utils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 16:04
 */
//函数式接口注解
@FunctionalInterface
public interface SFunction<T> extends Serializable {

    Object get(T source);

}
