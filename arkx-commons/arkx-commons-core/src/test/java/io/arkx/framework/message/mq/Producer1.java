package io.arkx.framework.message.mq;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.arkx.framework.message.tcp.Config;

public class Producer1 {

	private static Logger logger = LoggerFactory.getLogger(Producer1.class);

	public static void main(String[] args) throws Exception {
		String serverAddress = Config.get("serverAddress");
		Producer producer = new Producer(serverAddress, "ArkMQ-Topic-1");

		producer.start();

		logger.info(StringUtils.center("ArkMQProducer1 消息发送开始", 50, "*"));

		for (int i = 0; i < 100; i++) {
			producer.sendMessage("Hello ArkMQ From Producer1[" + i + "]");
			logger.info("ArkMQProducer1 发送消息编号:" + i);

			Thread.sleep(100);
		}

		producer.shutdown();
		logger.info(StringUtils.center("ArkMQProducer1 消息发送完毕", 50, "*"));
	}

}
