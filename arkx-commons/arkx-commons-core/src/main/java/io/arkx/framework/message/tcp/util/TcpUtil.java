package io.arkx.framework.message.tcp.util;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class TcpUtil {

	public static SocketAddress string2SocketAddress(final String addr) {
        String[] s = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(s[0], Integer.valueOf(s[1]));
        return isa;
    }
}
