package io.arkx.framework.avatarmq;

import io.arkx.framework.avatarmq.consumer.AvatarMQConsumer;
import io.arkx.framework.avatarmq.consumer.ProducerMessageHook;
import io.arkx.framework.avatarmq.msg.ConsumerAckMessage;
import io.arkx.framework.avatarmq.msg.Message;

/**
 * @filename:AvatarMQConsumer2.java
 * @description:AvatarMQConsumer2功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AvatarMQConsumer2 {

    private static ProducerMessageHook hook = new ProducerMessageHook() {
        public ConsumerAckMessage hookMessage(Message message) {
            System.out.printf("AvatarMQConsumer2 收到消息编号:%s,消息内容:%s\n", message.getMsgId(), new String(message.getBody()));
            ConsumerAckMessage result = new ConsumerAckMessage();
            result.setStatus(ConsumerAckMessage.SUCCESS);
            return result;
        }
    };

    public static void main(String[] args) {
        AvatarMQConsumer consumer = new AvatarMQConsumer("127.0.0.1:18888", "AvatarMQ-Topic-2", hook);
        consumer.init();
        consumer.setClusterId("AvatarMQCluster2");
        consumer.receiveMode();
        consumer.start();
    }
}
