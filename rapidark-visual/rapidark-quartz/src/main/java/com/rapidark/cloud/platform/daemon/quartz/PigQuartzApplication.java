package com.rapidark.cloud.platform.daemon.quartz;

import com.rapidark.cloud.platform.common.feign.annotation.EnablePigFeignClients;
import com.rapidark.cloud.platform.common.security.annotation.EnablePigResourceServer;
import com.rapidark.cloud.platform.common.swagger.annotation.EnablePigDoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author frwcloud
 * @date 2023-07-05
 */
@EnablePigDoc("job")
@EnablePigFeignClients
@EnablePigResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class PigQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigQuartzApplication.class, args);
	}

}
