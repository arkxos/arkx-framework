package io.arkx.framework.commons.util;

import java.io.Serializable;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 16:04
 */
// 函数式接口注解
@FunctionalInterface
public interface SFunction<T> extends Serializable {

	Object get(T source);

}
