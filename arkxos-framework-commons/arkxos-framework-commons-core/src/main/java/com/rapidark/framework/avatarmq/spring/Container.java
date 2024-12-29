package com.rapidark.framework.avatarmq.spring;

/**
 * @filename:Container.java
 * @description:Container功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface Container {

    void start();

    void stop();

    Context<?> getContext();
}
