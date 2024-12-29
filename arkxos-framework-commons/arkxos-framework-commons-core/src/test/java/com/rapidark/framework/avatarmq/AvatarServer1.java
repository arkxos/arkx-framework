package com.arkxos.framework.avatarmq;

import com.arkxos.framework.avatarmq.broker.server.AvatarMQBrokerServer;

public class AvatarServer1 {

	public static void main(String[] args) {
		AvatarMQBrokerServer server = new AvatarMQBrokerServer("127.0.0.1:18888");
		server.init();
		server.start();
	}
}
