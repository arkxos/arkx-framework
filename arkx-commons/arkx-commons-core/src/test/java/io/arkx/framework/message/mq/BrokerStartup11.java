package io.arkx.framework.message.mq;

import io.arkx.framework.message.tcp.Config;

public class BrokerStartup11 {

    public static void main(String[] args) throws Exception {
        String serverAddress = Config.get("serverAddress");

        new Broker(serverAddress).start();
    }
}
