package io.arkx.framework.message.tcp.util;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class PortScan {

	/**
	 * 有两个方法测试可用端口号 1. 用new socket(address,port) 如果port被绑定，则会被IOException捕获，就可以在catch中处理。
	 * 如果没有被绑定，则直接输出端口就行 2.用new DatagramSocket(port) 原理和第一个方法一样 这个方法只能测试本地主机，但比较快捷，而且代码简单
	 * 另外端口1~5000已被只固定业务，所以只用测试5001~65535
	 */
	@SuppressWarnings({ "resource", "unused" })
	public static int findUnusedPort() {
		InetAddress inet = null;
		try {
			inet = InetAddress.getByName("127.0.0.1");
		}
		catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		// System.out.println("Scanning ports");

		for (int i = 5001; i < 65536; i++) {

			// try {
			// Socket s=new Socket(inet,i);
			// System.out.println("enabled port:"+i);
			// } catch (IOException e) {
			// //e.printStackTrace();
			// System.out.println("busy port:"+i);
			// }

			try {
				new DatagramSocket(i);
				// System.out.println("enabled port:"+i);
				return i;
			}
			catch (SocketException e) {
				// e.printStackTrace();
				// System.out.println("busy port:"+i);
			}
		}

		return -1;
	}

	public static void main(String[] args) {
		System.out.println(findUnusedPort());
	}

}
