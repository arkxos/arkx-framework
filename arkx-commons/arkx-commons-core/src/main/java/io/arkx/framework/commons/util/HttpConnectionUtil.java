package io.arkx.framework.commons.util;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * @author Darkness
 * @date 2015-8-25 下午12:56:23
 * @version V1.0
 * @since infinity 1.0
 */
public class HttpConnectionUtil {

	public static String readText(String connectionUrl) {
		return readText(connectionUrl, null);
	}

	public static String readText(String connectionUrl, String charset) {
		return readText(connectionUrl, charset, 0);
	}

	public static String readText(String url, String charset, int tryTimes) {
		int i = 0;
		while (i++ <= tryTimes) {
			try {
				return requestText(url, charset);
			}
			catch (Exception e1) {
				if (e1 instanceof SocketTimeoutException) {
					System.out.println("try times:" + i);
					try {
						Thread.sleep(200);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					e1.printStackTrace();
				}
			}
		}
		return "";
	}

	public static String requestText(String url, String charset) throws IOException {
		Connection connection = Jsoup.connect(url);
		connection.ignoreContentType(true).get();

		String responseBody = "";

		if (charset == null) {
			responseBody = connection.response().body();
		}
		else {
			responseBody = new String(connection.response().bodyAsBytes(), charset);
		}

		return responseBody;
	}

}
