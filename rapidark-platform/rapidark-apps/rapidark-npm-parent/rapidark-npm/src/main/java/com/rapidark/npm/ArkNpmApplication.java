package com.rapidark.npm;

import com.rapidark.framework.boot.RapidArkApplication;
import com.rapidark.npm.cdn.CDNServlet;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@ServletComponentScan(basePackages = "com.rapidark.npm.cdn", basePackageClasses = { CDNServlet.class })
@EnableDiscoveryClient
@SpringBootApplication
public class ArkNpmApplication {

	public static void main(String[] args) {
		RapidArkApplication.run(ArkNpmApplication.class, args);
	}

}
