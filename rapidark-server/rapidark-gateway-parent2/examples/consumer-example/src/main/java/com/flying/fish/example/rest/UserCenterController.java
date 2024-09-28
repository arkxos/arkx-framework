package com.flying.fish.example.rest;

//import com.flying.fish.example.feign.OpenFeignService;
import com.flying.fish.example.feign.OpenFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @Description 演示示例，模拟《用户管理系统》接口
 * @Author JL
 * @Date 2020/12/30
 * @Version V1.0
 */
@Slf4j
@RestController
@RequestMapping("/userCenter")
public class UserCenterController {

    @Value("${server.port}")
    private String port;

    @Autowired
    private OpenFeignService openFeignService;

    @Autowired
    private RestTemplate restTemplate;

    @Qualifier
    @Autowired
    private WebClient.Builder clientBuilder;

    /**
     * 提供外部调用API（结合Feign示例演示，http://127.0.0.1:8771/route/userCenter/feign/getUser）
     * @return
     */
    @RequestMapping(value = "/feign/getUser")
    public String getFeignUser() {
        log.info("http to /feign/getUser");
        return openFeignService.getUser();
    }

    /**
     * 提供外部调用API（结合RestTemplate示例演示，http://127.0.0.1:8771/route/userCenter/restTemplate/getUser）
     * @return
     */
    @RequestMapping(value = "/restTemplate/getUser")
    public String getRestTemplateUser() {
        log.info("http to /restTemplate/getUser");
        return restTemplate.getForObject("http://provider-examples/userCenter/getUser", String.class);
    }

    /**
     * 提供外部调用API（结合WebClient示例演示，http://127.0.0.1:8771/route/userCenter/webClient/getUser）
     * @return
     */
    @RequestMapping(value = "/webClient/getUser")
    public Mono<String> getWebClientUser() {
        log.info("http to /webClient/getUser");
        return clientBuilder.baseUrl("http://provider-examples").build().get().uri("/userCenter/getUser").retrieve().bodyToMono(String.class);
    }

}
