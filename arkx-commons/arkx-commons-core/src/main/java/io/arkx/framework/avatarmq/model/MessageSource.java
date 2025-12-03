package io.arkx.framework.avatarmq.model;

/**
 * @filename:MessageSource.java
 * @description:MessageSource功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public enum MessageSource {

    AvatarMQConsumer(1), AvatarMQBroker(2), AvatarMQProducer(3);

    private int source;

    private MessageSource(int source) {
        this.source = source;
    }

    public int getSource() {
        return source;
    }

}
