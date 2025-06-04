package io.arkx.framework.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统信息类
 * 
 * @author Darkness
 * @date 2012-8-6 下午9:43:55
 * @version V1.0
 */
public class SystemInfo {
	
	public static final String execCmd(String cmd) throws IOException, InterruptedException {
		Process proc = Runtime.getRuntime().exec(cmd);
		InputStream is = proc.getInputStream();
		Thread.sleep(5000L);
		String output = FileUtil.readText(is, getFileEncode());
		return output;
	}
	
	public static String getFileEncode() {
		return System.getProperty("file.encoding");
	}
	
	public static String userHome() {
		return System.getProperty("user.home");
	}
	
	public static String userDir() {
		return System.getProperty("user.dir");
	}
	
	/*
	 * @return true---是Windows操作系统
	 */
	public static boolean isWindows() {
		boolean isWindowsOS = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			isWindowsOS = true;
		}
		return isWindowsOS;
	}
	
	public static boolean isMac() {
		boolean isMac = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("mac") > -1) {
			isMac = true;
		}
		return isMac;
	}
	
	public static final String osName() {
		return System.getProperty("os.name");
	}

	private static String macAddress;
	
	public static final String macAddress() {
		if (!StringUtil.isEmpty(macAddress)) {
			return macAddress;
		}
		String os = osName().toLowerCase();
		String output = null;
		try {
			String cmd = "ipconfig /all";
			if (os.indexOf("windows") < 0) {
				cmd = "ifconfig";
			}
			output = execCmd(cmd);
		} catch (Exception ex) {
			String cmd = "ipconfig /all";
			if (os.indexOf("windows") < 0) {
				cmd = "/sbin/ifconfig";// 尝试/sbin/ifconfig
			}
			
			try {
				output = execCmd(cmd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (StringUtil.isEmpty(output)) {
			String cmd = "ip addr";// 尝试ip addr
			try {
				output = execCmd(cmd);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// mac地址00开头的在solaris上会只有1个0
		Pattern p = Pattern.compile("([0-9A-Fa-f]{1,2}[\\:\\-]){5}[0-9A-Fa-f]{2}", Pattern.DOTALL);
		Matcher m = p.matcher(output);
		int lastIndex = 0;
		StringBuffer sb = new StringBuffer();
//		while (m.find(lastIndex)) {
//			if (m.end() < output.length() - 1) {
//				String next = output.substring(m.end(), m.end() + 1);
//				if (ObjectUtil.in(next, "-", ":")) {
//					lastIndex = m.end();
//					continue;
//				}
//			}
//			if (lastIndex != 0) {
//				sb.append(",");
//			}
//			sb.append(m.group(0));
//			lastIndex = m.end();
//		}
		if (m.find(lastIndex)) {
			if (lastIndex != 0) {
				sb.append(",");
			}
			sb.append(m.group(0));
			lastIndex = m.end();
		}
		macAddress = sb.toString().replace(':', '-');
		return macAddress;
	}

	private static String ip;
	
	/**
	 * 获取本机IP地址，需要处理：
		1. 多块网卡。
		2. 排除loopback设备、虚拟网卡
	 * @return
	 */
	public static final String ip() {
		if (!StringUtil.isEmpty(ip)) {
			return ip;
		}
		try {
			ip = Addressing.getIp4Address().getHostAddress();
			return ip;
		} catch (Exception e) {
			e.printStackTrace();
		}
		ip = "127.0.0.1";
		return ip;
	}
	
	private static String hardwareID;
	
	public static String hardwareID() {
		if (!StringUtil.isEmpty(hardwareID)) {
			return hardwareID;
		}
		String os = osName().toLowerCase();
		String output = null;
		try {
			if(isMac()) {
				String cmd = "sysctl -a machdep.cpu";
				Process proc = Runtime.getRuntime().exec(cmd);
				proc.getOutputStream().close();
				InputStream is = proc.getInputStream();
				Thread.sleep(3000L);
				output = FileUtil.readText(is, "UTF-8");
				if (output.indexOf("\n") > 0) {
					output = output.substring(output.indexOf("\n") + 1);
				}
				
//				output = output.replaceAll("\\s", "");
				List<String> list = new ArrayList<String>();
				
				List<String> keyMap = new ArrayList<>();
				keyMap.add("machdep.cpu.vendor");
				keyMap.add("machdep.cpu.family");
				keyMap.add("machdep.cpu.model");
				keyMap.add("machdep.cpu.brand_string");
				keyMap.add("machdep.cpu.features");
				for (String line : output.split("\\n")) {
					line = line.toLowerCase();
					for (String key : keyMap) {
						if(line.startsWith(key)) {
							list.add(line);	
							break;
						}
 					}
				}
				list = ObjectUtil.sort(list, ObjectUtil.ASCStringComparator);
				output = StringUtil.join(list.toArray());
				output = StringUtil.md5Hex(output).substring(0, 8);
			} else if (os.indexOf("windows") < 0) {
				output = FileUtil.readText("/proc/cpuinfo");
				List<String> list = new ArrayList<String>();
				for (String line : output.split("\\n")) {
					line = line.toLowerCase();
					if ((line.startsWith("vendor")) || (line.startsWith("cpu family")) || (line.startsWith("model"))) {
						list.add(line);
					}
				}
				list = ObjectUtil.sort(list, ObjectUtil.ASCStringComparator);
				output = StringUtil.join(list.toArray());
				output = StringUtil.md5Hex(output).substring(0, 8);
			} else {
				String cmd = "wmic cpu get processorid";
				Process proc = Runtime.getRuntime().exec(cmd);
				proc.getOutputStream().close();
				InputStream is = proc.getInputStream();
				Thread.sleep(3000L);
				output = FileUtil.readText(is, "UTF-8");
				if (output.indexOf("\n") > 0) {
					output = output.substring(output.indexOf("\n") + 1);
				}
				output = output.replaceAll("\\s", "");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Get Hardware ID Failed:OS=" + os);
		}
		hardwareID = output.toUpperCase();
		return hardwareID;
	}

	public static final void main(String[] args) {
		System.out.println("User Home: " + userHome());
		System.out.println("Operating System: " + osName());
		System.out.println("IP/Localhost: " + ip());
		System.out.println("MAC Address: " + macAddress());
		System.out.println("Hardware ID: " + hardwareID());
	}
}
