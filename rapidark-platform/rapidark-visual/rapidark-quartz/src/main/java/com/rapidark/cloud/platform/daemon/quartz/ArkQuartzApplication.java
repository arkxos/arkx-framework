package com.rapidark.cloud.platform.daemon.quartz;

import com.rapidark.cloud.platform.common.core.boot.RapidArkApplication;
import com.rapidark.cloud.platform.common.feign.annotation.EnableArkFeignClients;
import com.rapidark.cloud.platform.common.security.annotation.EnableArkResourceServer;
import com.rapidark.cloud.platform.common.swagger.annotation.EnableArkDoc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author frwcloud
 * @date 2023-07-05
 */
@EnableArkDoc("job")
@EnableArkFeignClients
@EnableArkResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class ArkQuartzApplication {

	public static void main(String[] args) {
		RapidArkApplication.run(ArkQuartzApplication.class, args);
	}

}
