package com.arkxos.framework.avatarmq.consumer;

import com.arkxos.framework.avatarmq.core.AvatarMQAction;
import com.arkxos.framework.avatarmq.core.MessageIdGenerator;
import com.arkxos.framework.avatarmq.core.MessageSystemConfig;
import com.arkxos.framework.avatarmq.model.MessageType;
import com.arkxos.framework.avatarmq.model.RequestMessage;
import com.arkxos.framework.avatarmq.msg.SubscribeMessage;
import com.arkxos.framework.avatarmq.msg.UnSubscribeMessage;
import com.arkxos.framework.avatarmq.netty.MessageProcessor;
import com.google.common.base.Joiner;

/**
 * 消息消费者
 * @filename:AvatarMQConsumer.java
 * @description:AvatarMQConsumer功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AvatarMQConsumer extends MessageProcessor implements AvatarMQAction {

    private ProducerMessageHook hook;
    private String brokerServerAddress;
    private String topic;
    private boolean subscribeMessage = false;
    private boolean running = false;
    private String defaultClusterId = "AvatarMQConsumerClusters";
    private String clusterId = "";
    private String consumerId = "";

	// 连接的消息服务器broker的ip地址以及关注的生产过来的消息钩子
    public AvatarMQConsumer(String brokerServerAddress, String topic, ProducerMessageHook hook) {
        super(brokerServerAddress);
        this.hook = hook;
        this.brokerServerAddress = brokerServerAddress;
        this.topic = topic;
    }

	// 向消息服务器broker发送取消订阅消息
    private void unRegister() {
        RequestMessage request = new RequestMessage();
        request.setMsgType(MessageType.AvatarMQUnsubscribe);
        request.setMsgId(new MessageIdGenerator().generate());
        request.setMsgParams(new UnSubscribeMessage(consumerId));
        sendSyncMessage(request);
        super.getMessageConnectFactory().close();
        super.closeMessageConnectFactory();
        running = false;
    }

	// 向消息服务器broker发送订阅消息
    private void register() {
        RequestMessage request = new RequestMessage();
        request.setMsgType(MessageType.AvatarMQSubscribe);
        request.setMsgId(new MessageIdGenerator().generate());

        SubscribeMessage subscript = new SubscribeMessage();
        subscript.setClusterId((clusterId.equals("") ? defaultClusterId : clusterId));
        subscript.setTopic(topic);
        subscript.setConsumerId(consumerId);

        request.setMsgParams(subscript);

        sendAsynMessage(request);
    }

    public void init() {
        super.getMessageConnectFactory().setMessageHandle(new MessageConsumerHandler(this, new ConsumerHookMessageEvent(hook)));
        Joiner joiner = Joiner.on(MessageSystemConfig.MessageDelimiter).skipNulls();
        consumerId = joiner.join((clusterId.equals("") ? defaultClusterId : clusterId), topic, new MessageIdGenerator().generate());
    }

	// 连接消息服务器broker
    public void start() {
        if (isSubscribeMessage()) {
            super.getMessageConnectFactory().connect();
            register();
            running = true;
        }
    }

    public void receiveMode() {
        setSubscribeMessage(true);
    }

    public void shutdown() {
        if (running) {
            unRegister();
        }
    }

    public String getBrokerServerAddress() {
        return brokerServerAddress;
    }

    public void setBrokerServerAddress(String brokerServerAddress) {
        this.brokerServerAddress = brokerServerAddress;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isSubscribeMessage() {
        return subscribeMessage;
    }

    public void setSubscribeMessage(boolean subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }

    public String getDefaultClusterId() {
        return defaultClusterId;
    }

    public void setDefaultClusterId(String defaultClusterId) {
        this.defaultClusterId = defaultClusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }
}
