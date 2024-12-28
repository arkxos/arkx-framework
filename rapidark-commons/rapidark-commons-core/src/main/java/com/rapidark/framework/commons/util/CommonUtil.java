package com.rapidark.framework.commons.util;

import java.io.File;

public class CommonUtil {
	
	public static String userDir() {
		return System.getProperty("user.dir") + File.separator;
	}

	public static void main(String[] args) {
		System.out.println(userDir());
	}
}
