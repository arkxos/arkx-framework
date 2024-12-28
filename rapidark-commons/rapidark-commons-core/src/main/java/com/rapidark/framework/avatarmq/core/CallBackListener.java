package com.rapidark.framework.avatarmq.core;

/**
 * @filename:CallBackListener.java
 * @description:CallBackListener功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public interface CallBackListener<T> {

    void onCallBack(T t);

}
