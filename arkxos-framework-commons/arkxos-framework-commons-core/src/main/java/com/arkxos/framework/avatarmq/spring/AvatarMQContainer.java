package com.arkxos.framework.avatarmq.spring;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @filename:AvatarMQContainer.java
 * @description:AvatarMQContainer功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AvatarMQContainer implements Container {

    public static final String AvatarMQConfigFilePath = "classpath:com/rapidark/avatarmq/spring/avatarmq-broker.xml";

    private AvatarMQContext springContext;

    public void start() {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext(AvatarMQConfigFilePath);
        springContext = new AvatarMQContext(context);
        context.start();
    }

    public void stop() {
        if (null != springContext && null != springContext.get()) {
            springContext.get().close();
            springContext = null;
        }
    }

    public AvatarMQContext getContext() {
        return springContext;
    }
}
