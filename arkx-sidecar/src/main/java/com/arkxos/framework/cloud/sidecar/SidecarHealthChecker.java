/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arkxos.framework.cloud.sidecar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Schedulers;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author www.itmuch.com
 * @author yuhuangbin
 */
public class SidecarHealthChecker {

	private static final Logger log = LoggerFactory.getLogger(SidecarHealthChecker.class);

	private final Map<String, SidecarInstanceInfo> sidecarInstanceCacheMap = new ConcurrentHashMap<>();

	private final SidecarDiscoveryClient sidecarDiscoveryClient;

	private final HealthIndicator healthIndicator;

	private final SidecarProperties sidecarProperties;

	private final ConfigurableEnvironment environment;

	@Autowired
	private ObjectProvider<CustomHealthCheckHandler> customHealthCheckHandlerObjectProvider;

	public SidecarHealthChecker(SidecarDiscoveryClient sidecarDiscoveryClient,
			HealthIndicator healthIndicator, SidecarProperties sidecarProperties,
			ConfigurableEnvironment environment) {
		this.sidecarDiscoveryClient = sidecarDiscoveryClient;
		this.healthIndicator = healthIndicator;
		this.sidecarProperties = sidecarProperties;
		this.environment = environment;
	}

	public void check() {
		Schedulers.single().schedulePeriodically(() -> {
			Multimap<String, SidecarConfig> serviceConfigMap = ArrayListMultimap.create();
			for (SidecarConfig sidecarConfig : sidecarProperties.getProxyList()) {

//				String applicationName = environment.getProperty("spring.application.name");
				String applicationName = sidecarConfig.getName();
						String ip = sidecarConfig.getIp();
				Integer port = sidecarConfig.getPort();

				Status status = healthIndicator.health().getStatus();

				SidecarInstanceInfo sidecarInstanceInfo = instanceCache(applicationName, ip,
						port, status);
				if (status.equals(Status.UP)) {
					serviceConfigMap.put(applicationName, sidecarConfig);
					// 存在同名服务
					if(serviceConfigMap.containsKey(applicationName)) {
						List<Instance> serviceInstance = new ArrayList<>();
						for (SidecarConfig config : serviceConfigMap.get(applicationName)) {
							Instance instance = new Instance();
							instance.setIp(config.getIp());
							instance.setPort(config.getPort());
							instance.setWeight(1.0);
							instance.setClusterName(Constants.DEFAULT_CLUSTER_NAME);

							serviceInstance.add(instance);
						}

						if (needRegister(applicationName, ip + ":" + port, sidecarInstanceInfo)) {
							this.sidecarDiscoveryClient.batchRegisterInstance(applicationName, "DEFAULT_GROUP",
									serviceInstance);
							log.info(
									"Polyglot service changed and Health check success. register the new instance. applicationName = {}, ip = {}, port = {}, status = {}",
									applicationName, ip, port, status);
						}
					} else {
						if (needRegister(applicationName, ip + ":" + port, sidecarInstanceInfo)) {
							this.sidecarDiscoveryClient.registerInstance(applicationName, ip,
									port);
							log.info(
									"Polyglot service changed and Health check success. register the new instance. applicationName = {}, ip = {}, port = {}, status = {}",
									applicationName, ip, port, status);
						}
					}
				}
				else {
					log.warn(
							"Health check failed. unregister this instance. applicationName = {}, ip = {}, port = {}, status = {}",
							applicationName, ip, port, status);
					this.sidecarDiscoveryClient.deregisterInstance(applicationName, ip, port);

					sidecarInstanceCacheMap.put(applicationName,
							buildCache(ip, port, status));
				}

				try {
					customHealthCheckHandlerObjectProvider
							.ifAvailable(customHealthCheckHandler -> customHealthCheckHandler
									.handler(applicationName, sidecarInstanceInfo));
				}
				catch (Exception e) {
					// ignore
				}
			}

		}, 0, sidecarProperties.getHealthCheckInterval(), TimeUnit.MILLISECONDS);
	}

	private SidecarInstanceInfo instanceCache(String applicationName, String ip,
			Integer port, Status status) {
		SidecarInstanceInfo sidecarInstanceInfo = buildCache(ip, port, status);
		sidecarInstanceCacheMap.putIfAbsent(applicationName, sidecarInstanceInfo);
		return sidecarInstanceInfo;
	}

	private boolean needRegister(String applicationName, String ipPort,
			SidecarInstanceInfo sidecarInstanceInfo) {
		String serviceId = applicationName+"_"+ipPort;
		SidecarInstanceInfo cacheRecord = sidecarInstanceCacheMap.get(serviceId);
		if (!Objects.equals(sidecarInstanceInfo, cacheRecord)) {
			// modify the cache info
			sidecarInstanceCacheMap.put(serviceId, sidecarInstanceInfo);
			return true;
		}
		return false;
	}

	private SidecarInstanceInfo buildCache(String ip, Integer port, Status status) {
		SidecarInstanceInfo cache = new SidecarInstanceInfo();
		cache.setIp(ip);
		cache.setPort(port);
		cache.setStatus(status);
		return cache;
	}

}
