package com.rapidark.framework.message.mq;

import com.arkxos.framework.message.mq.Broker;
import com.rapidark.framework.message.tcp.Config;

public class BrokerStartup11 {

	public static void main(String[] args) throws Exception {
		String serverAddress = Config.get("serverAddress");

		new Broker(serverAddress).start();
	}
}
