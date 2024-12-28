package com.rapidark.framework.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Darkness
 * @date 2015年12月6日 上午11:59:27
 * @version V1.0
 * @since infinity 1.0
 */
public class NetTester {

	private static Logger logger = LoggerFactory.getLogger(NetTester.class);
	
	public static void main(String[] args) {
		System.out.println(NetTester.isNetworkConnected());
	}

	static boolean connected = false;

	public static boolean isNetworkConnected() {
		int infCount = 0;
		try {
			// firstly check the network interfaces.
			Enumeration<NetworkInterface> netints = NetworkInterface.getNetworkInterfaces();
			while (netints.hasMoreElements()) {
				NetworkInterface intf = netints.nextElement();
				if (intf.isUp() && !intf.isLoopback()) {
					infCount++;
				}
			}
			// no available network interfaces
			if (infCount == 0) {
				// if needed.
				logger.info("你似乎把网线拔掉了？");
				return false;
			}
			// if there exist some active interface. test it
			// ping `www.baidu.com
			connected = false;
			new Thread(new Runnable() {
				public void run() {
					try {
						Runtime runtime = Runtime.getRuntime();
						Process p = runtime.exec("ping www.baidu.com");
						InputStreamReader reader = new InputStreamReader(p.getInputStream());
						BufferedReader buf = new BufferedReader(reader);
						
						if(buf.readLine() != null) {
							connected = true;	
						} else {
							connected = false;
						}
						System.out.println(); // empty line
						System.out.println(buf.readLine()); // prompt for this command
						System.out.println(buf.readLine()); // real output
						
						logger.info("net is " + (connected ? " connected " : " disconnected "));
						buf.close();
						reader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
			// wait for 1000millsecond
			Thread.sleep(3000);
			//logger.info("net is finish");
			return connected;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}
}
