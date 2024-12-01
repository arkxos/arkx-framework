/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rapidark.cloud.platform.gateway;

import com.rapidark.framework.boot.RapidArkApplication;
import com.rapidark.framework.data.jpa.BaseRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lengleng
 * @date 2018年06月21日
 * <p>
 * 网关应用
 */
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.rapidark")
@EnableJpaRepositories(
		basePackages = { "com.xdreamaker", "com.rapidark" },
		repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EntityScan({
		"com.rapidark.cloud.platform.gateway.framework.entity"
})
public class ArkGatewayApplication {

	public static void main(String[] args) {
		RapidArkApplication.run(ArkGatewayApplication.class, args);
	}

}
