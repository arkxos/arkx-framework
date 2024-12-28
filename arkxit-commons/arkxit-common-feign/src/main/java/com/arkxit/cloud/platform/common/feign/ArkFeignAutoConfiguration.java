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

package com.arkxit.cloud.platform.common.feign;

//import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import com.arkxit.cloud.platform.common.feign.core.ArkFeignInnerRequestInterceptor;
import com.arkxit.cloud.platform.common.feign.core.ArkFeignRequestCloseInterceptor;
//import com.rapidark.cloud.platform.common.feign.sentinel.ext.ArkSentinelFeign;

import org.springframework.cloud.openfeign.ArkFeignClientsRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * sentinel 配置
 *
 * @author lengleng
 * @date 2020-02-12
 */
@Configuration(proxyBeanMethods = false)
@Import(ArkFeignClientsRegistrar.class)
//@AutoConfigureBefore(SentinelFeignAutoConfiguration.class)
public class ArkFeignAutoConfiguration {

//	@Bean
//	@Scope("prototype")
//	@ConditionalOnMissingBean
//	@ConditionalOnProperty(name = "feign.sentinel.enabled")
//	public Feign.Builder feignSentinelBuilder() {
//		return ArkSentinelFeign.builder();
//	}

	/**
	 * add http connection close header
	 * @return
	 */
	@Bean
	public ArkFeignRequestCloseInterceptor pigFeignRequestCloseInterceptor() {
		return new ArkFeignRequestCloseInterceptor();
	}

	/**
	 * add inner request header
	 * @return PigFeignInnerRequestInterceptor
	 */
	@Bean
	public ArkFeignInnerRequestInterceptor pigFeignInnerRequestInterceptor() {
		return new ArkFeignInnerRequestInterceptor();
	}

}
