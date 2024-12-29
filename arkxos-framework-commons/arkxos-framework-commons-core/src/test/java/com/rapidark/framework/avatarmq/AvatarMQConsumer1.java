package com.rapidark.framework.avatarmq;

import com.arkxos.framework.avatarmq.consumer.AvatarMQConsumer;
import com.arkxos.framework.avatarmq.consumer.ProducerMessageHook;
import com.arkxos.framework.avatarmq.msg.ConsumerAckMessage;
import com.arkxos.framework.avatarmq.msg.Message;

/**
 * @filename:AvatarMQConsumer1.java
 * @description:AvatarMQConsumer1功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AvatarMQConsumer1 {

    private static ProducerMessageHook hook = new ProducerMessageHook() {
    	@Override
        public ConsumerAckMessage hookMessage(Message message) {
            System.out.printf("AvatarMQConsumer1 收到消息编号:%s,消息内容:%s\n", message.getMsgId(), new String(message.getBody()));
            return ConsumerAckMessage.createSuccessAckMessage();
        }
    };

    public static void main(String[] args) {
        AvatarMQConsumer consumer = new AvatarMQConsumer("127.0.0.1:18888", "AvatarMQ-Topic-1", hook);
        consumer.init();
        consumer.setClusterId("AvatarMQCluster");
        consumer.receiveMode();
        consumer.start();
    }
}
