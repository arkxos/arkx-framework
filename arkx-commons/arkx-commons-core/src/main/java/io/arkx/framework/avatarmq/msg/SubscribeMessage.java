package io.arkx.framework.avatarmq.msg;

import java.io.Serializable;

/**
 * @filename:SubscribeMessage.java
 * @description:SubscribeMessage功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class SubscribeMessage extends BaseMessage implements Serializable {

    private String clusterId;
    private String topic;
    private String consumerId;

    public SubscribeMessage() {
        super();
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
}
