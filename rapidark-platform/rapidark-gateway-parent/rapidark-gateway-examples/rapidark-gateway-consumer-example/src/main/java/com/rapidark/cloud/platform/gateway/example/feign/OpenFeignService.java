package com.rapidark.cloud.platform.gateway.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单，使用Feign，只需要创建一个接口并注解
 * @Author JL
 * @Date 2021/02/01
 * @Version V1.0
 */
//examples 等同于 eureka注册中的 lb://EXAMPLES 服务名，/userCenter为ProducerController中的请求URL的RequestMapping映射前缀
//@FeignClient(value = "examples", path = "/userCenter")
@FeignClient(name = "provider-examples", configuration = OpenFeignConfig.class)
//@FeignClient(name = "provider-examples", url = "http://192.168.11.45:8769", configuration = OpenFeignConfig.class)
public interface OpenFeignService {

    /**
     Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。
         1.Feign 采用的是基于接口的注解
         2.Feign 整合了ribbon，具有负载均衡的能力，通过服务名识别注册到eureka的服务，进行负载调用
         3.整合了Hystrix，具有熔断的能力
     */

    /**
     * 在gateway服务中配置lb://EXAMPLES 服务注册地址，feign接口在被调用时，通过EXAMPLES服务名选择其中服务组装地址在进行调用
     * 远程调用 http://127.0.0.1:8769/userCenter/getUser 接口
     * @return
     */
    @RequestMapping(value = "/userCenter/getUser", method = RequestMethod.GET)
    String getUser();

}
