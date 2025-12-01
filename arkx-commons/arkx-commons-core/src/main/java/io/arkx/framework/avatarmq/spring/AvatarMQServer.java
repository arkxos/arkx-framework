package io.arkx.framework.avatarmq.spring;

import io.arkx.framework.avatarmq.broker.server.AvatarMQBrokerServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @filename:AvatarMQServer.java
 * @description:AvatarMQServer功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AvatarMQServer extends AvatarMQBrokerServer implements ApplicationContextAware, InitializingBean {

    public AvatarMQServer(String serverAddress) {
        super(serverAddress);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.printf("AvatarMQ Server Start Success![author tangjie]\n");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
        start();
    }
}
