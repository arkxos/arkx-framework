package io.arkx.framework.message.tcp;

import io.arkx.framework.commons.util.UuidUtil;
import io.arkx.framework.message.tcp.client.NettyClient;
import io.arkx.framework.message.tcp.struct.NettyMessage;
import io.arkx.framework.message.tcp.struct.RequestMessage;

/**
 * @author Darkness
 * @date 2017年4月11日 下午4:33:28
 * @version 1.0
 * @since 1.0 
 */
public class LightingClient1 extends NettyClient {

	public LightingClient1(String serverAddress) {
		super(serverAddress);
	}

	public void openLight(String lightingId) {
		System.out.println("open light:" + lightingId);
		NettyMessage message = new RequestMessage(UuidUtil.base58Uuid());
		message.setBusinessType(BusinesMessageType.OpenLight.value());
		
		message.setBody(lightingId.getBytes());
		
		sendMessage(message);
	}
	
	public void closeLight(String lightingId) {
		NettyMessage message = new RequestMessage(UuidUtil.base58Uuid());
		message.setBusinessType(BusinesMessageType.CloseLight.value());
		
		message.setBody(lightingId.getBytes());
		
		sendMessage(message);
	}
	
	public static void main(String[] args) throws Exception {
		// 服务器 ip
		String serverAddress = Config.get("serverAddress");

		// 灯控制客户端
		LightingClient1 client = new LightingClient1(serverAddress);
		client.start();
		
		String lightingId5 = "0051006001002";//"0301001003000";//018 1007 005 002	18巷道7列5层 二楼
		String lightingId6 = "0061002005002";
		
		for (int i = 0; i < 1; i++) {
			String lightingId = i % 2 == 0 ? lightingId5 : lightingId6;
			// 灯点亮
			client.openLight(lightingId);

//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			// 灯关闭
			client.closeLight(lightingId);
		}
		
	}
	
}
