package io.arkx.framework.message.tcp;

import io.arkx.framework.message.tcp.server.NettyServer;

/**
 * io.arkx.lighting.LightingServer
 *
 * @author Darkness
 * @date 2017年4月11日 下午4:33:28
 * @version 1.0
 * @since 1.0
 */
public class LightingServer1 extends NettyServer {

    public LightingServer1(String Address) {
        super(Address);
    }

    public static void main(String[] args) throws Exception {
        String serverAddress = Config.get("serverAddress");

        new LightingServer1(serverAddress).start();
    }

}
