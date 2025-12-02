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

package io.arkx.framework.cloud.sidecar.nacos;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.arkx.framework.cloud.sidecar.SidecarDiscoveryClient;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

/**
 * @author www.itmuch.com
 */
public class SidecarNacosDiscoveryClient implements SidecarDiscoveryClient {

    private static final Logger log = LoggerFactory.getLogger(SidecarNacosDiscoveryClient.class);

    private NacosServiceManager nacosServiceManager;

    private final SidecarNacosDiscoveryProperties sidecarNacosDiscoveryProperties;

    public SidecarNacosDiscoveryClient(NacosServiceManager nacosServiceManager,
            SidecarNacosDiscoveryProperties sidecarNacosDiscoveryProperties) {
        this.nacosServiceManager = nacosServiceManager;
        this.sidecarNacosDiscoveryProperties = sidecarNacosDiscoveryProperties;
    }

    @Override
    public void registerInstance(String applicationName, String ip, Integer port) {
        try {
            Instance instance = new Instance();
            instance.setIp(ip);
            instance.setPort(port);
            instance.setWeight(1.0);
            instance.setClusterName(Constants.DEFAULT_CLUSTER_NAME);
            // registerInstance(serviceName, groupName, instance);
            // this.namingService().batchRegisterInstance().registerInstance(applicationName,
            // sidecarNacosDiscoveryProperties.getGroup(), ip, port);
            this.namingService().batchRegisterInstance(applicationName, sidecarNacosDiscoveryProperties.getGroup(),
                    Arrays.asList(instance));
            log.debug("register instance[" + applicationName + "," + ip + "," + port + "] success");
        } catch (NacosException e) {
            log.warn("nacos exception happens", e);
        }
    }

    @Override
    public void deregisterInstance(String applicationName, String ip, Integer port) {
        try {
            this.namingService().deregisterInstance(applicationName, sidecarNacosDiscoveryProperties.getGroup(), ip,
                    port);
            log.debug("deregister instance[" + applicationName + "," + ip + "," + port + "] success");
        } catch (NacosException e) {
            log.warn("nacos exception happens", e);
        }
    }

    @Override
    public void batchRegisterInstance(String applicationName, String groupName, List<Instance> instances) {
        try {
            this.namingService().batchRegisterInstance(applicationName, groupName, instances);
            // log.debug("register instance["+applicationName+","+ip+","+port+"] success");
        } catch (NacosException e) {
            log.warn("nacos exception happens", e);
        }
    }

    // @Override
    // public void deregisterInstance(String applicationName, String ip, Integer
    // port) {
    // try {
    // this.namingService().deregisterInstance(applicationName,
    // sidecarNacosDiscoveryProperties.getGroup(), ip, port);
    // log.debug("deregister instance["+applicationName+","+ip+","+port+"]
    // success");
    // }
    // catch (NacosException e) {
    // log.warn("nacos exception happens", e);
    // }
    // }

    private NamingService namingService() {
        return nacosServiceManager.getNamingService();
    }

}
