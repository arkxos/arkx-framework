package com.arkxos.framework.avatarmq.producer;

import java.util.concurrent.atomic.AtomicLong;

import com.arkxos.framework.avatarmq.core.AvatarMQAction;
import com.arkxos.framework.avatarmq.model.MessageSource;
import com.arkxos.framework.avatarmq.model.MessageType;
import com.arkxos.framework.avatarmq.model.RequestMessage;
import com.arkxos.framework.avatarmq.model.ResponseMessage;
import com.arkxos.framework.avatarmq.msg.Message;
import com.arkxos.framework.avatarmq.msg.ProducerAckMessage;
import com.arkxos.framework.avatarmq.netty.MessageProcessor;

/**
 * 消息的生产者
 * @filename:AvatarMQProducer.java
 * @description:AvatarMQProducer功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AvatarMQProducer extends MessageProcessor implements AvatarMQAction {

    private boolean brokerConnect = false;
    private boolean running = false;
    private String brokerServerAddress;
    private String topic;
    private String defaultClusterId = "AvatarMQProducerClusters";
    private String clusterId = "";
    private AtomicLong msgId = new AtomicLong(0L);

	// 连接消息转发服务器broker的ip地址，以及生产出来消息附带的主题信息
	public AvatarMQProducer(String brokerServerAddress, String topic) {
		super(brokerServerAddress);
		this.brokerServerAddress = brokerServerAddress;
		this.topic = topic;
	}

	//没有连接上消息转发服务器broker就发送的话，直接应答失败
    private ProducerAckMessage checkMode() {
        if (!brokerConnect) {
            ProducerAckMessage ack = new ProducerAckMessage();
            ack.setStatus(ProducerAckMessage.FAIL);
            return ack;
        }

        return null;
    }

	// 启动消息生产者
	public void start() {
		super.getMessageConnectFactory().connect();
		brokerConnect = true;
		running = true;
	}

	//连接消息转发服务器broker，设定生产者消息处理钩子，用于处理broker过来的消息应答
    public void init() {
        ProducerHookMessageEvent hook = new ProducerHookMessageEvent();
        hook.setBrokerConnect(brokerConnect);
        hook.setRunning(running);
        super.getMessageConnectFactory().setMessageHandle(new MessageProducerHandler(this, hook));
    }

	// 投递消息API
    public ProducerAckMessage delivery(Message message) {
        if (!running || !brokerConnect) {
            return checkMode();
        }

        message.setTopic(topic);
        message.setTimeStamp(System.currentTimeMillis());

        RequestMessage request = new RequestMessage();
        request.setMsgId(String.valueOf(msgId.incrementAndGet()));
        request.setMsgParams(message);
        request.setMsgType(MessageType.AvatarMQMessage);
        request.setMsgSource(MessageSource.AvatarMQProducer);
        message.setMsgId(request.getMsgId());

        ResponseMessage response = (ResponseMessage) sendAsynMessage(request);
        if (response == null) {
            ProducerAckMessage ack = new ProducerAckMessage();
            ack.setStatus(ProducerAckMessage.FAIL);
            return ack;
        }

        ProducerAckMessage result = (ProducerAckMessage) response.getMsgParams();
        return result;
    }

	// 关闭消息生产者
    public void shutdown() {
        if (running) {
            running = false;
            super.getMessageConnectFactory().close();
            super.closeMessageConnectFactory();
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }
}
