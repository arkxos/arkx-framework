package com.rapidark.framework.message.tcp;

import com.arkxos.framework.message.tcp.server.NettyServer;

/**
 * com.xdreamaker.lighting.LightingServer
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
