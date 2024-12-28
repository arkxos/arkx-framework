package com.rapidark.framework.avatarmq.spring;

import org.springframework.context.support.AbstractApplicationContext;

/**
 * @filename:AvatarMQContext.java
 * @description:AvatarMQContext功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public final class AvatarMQContext implements Context<AbstractApplicationContext> {

    private final AbstractApplicationContext applicationContext;

    public AvatarMQContext(final AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public AbstractApplicationContext get() {
        return applicationContext;
    }
}
