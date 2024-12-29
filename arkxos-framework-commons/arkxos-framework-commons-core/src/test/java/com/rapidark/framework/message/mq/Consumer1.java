package com.rapidark.framework.message.mq;

import com.arkxos.framework.message.mq.Consumer;
import com.arkxos.framework.message.mq.MessageHandler;
import com.arkxos.framework.message.tcp.struct.NettyMessage;
import com.rapidark.framework.message.tcp.Config;

public class Consumer1 {

    private static MessageHandler handler = new MessageHandler() {
    	@Override
        public void handle(NettyMessage message) {
            System.out.printf("AvatarMQConsumer1 收到消息编号:,消息内容:%s\n", new String(message.getBody()));
        }
    };

    public static void main(String[] args) throws Exception {
    	String serverAddress = Config.get("serverAddress");
		
		Consumer consumer = new Consumer(serverAddress, "ArkMQ-Topic-1", handler);
		consumer.start();
    }
}
