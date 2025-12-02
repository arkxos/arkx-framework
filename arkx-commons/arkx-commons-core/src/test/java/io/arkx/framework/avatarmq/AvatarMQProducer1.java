package io.arkx.framework.avatarmq;

import org.apache.commons.lang3.StringUtils;

import io.arkx.framework.avatarmq.msg.Message;
import io.arkx.framework.avatarmq.msg.ProducerAckMessage;
import io.arkx.framework.avatarmq.producer.AvatarMQProducer;

/**
 * @filename:AvatarMQProducer1.java
 * @description:AvatarMQProducer1功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AvatarMQProducer1 {

	public static void main(String[] args) throws InterruptedException {
		AvatarMQProducer producer = new AvatarMQProducer("127.0.0.1:18888", "AvatarMQ-Topic-1");
		producer.setClusterId("AvatarMQCluster");
		producer.init();
		producer.start();

		System.out.println(StringUtils.center("AvatarMQProducer1 消息发送开始", 50, "*"));

		for (int i = 0; i < 1; i++) {
			Message message = new Message();
			String str = "Hello AvatarMQ From Producer1[" + i + "]";
			message.setBody(str.getBytes());
			ProducerAckMessage result = producer.delivery(message);
			if (result.isSuccess()) {
				System.out.printf("AvatarMQProducer1 发送消息编号:%s\n", result.getMsgId());
			}

			Thread.sleep(100);
		}

		producer.shutdown();
		System.out.println(StringUtils.center("AvatarMQProducer1 消息发送完毕", 50, "*"));
	}

}
