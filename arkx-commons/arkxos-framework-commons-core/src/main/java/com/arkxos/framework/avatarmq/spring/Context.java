package com.arkxos.framework.avatarmq.spring;

/**
 * @filename:Context.java
 * @description:Context功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface Context<T> {

    T get();
}
