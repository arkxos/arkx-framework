package com.rapidark.cloud.platform.gateway.example.rest;

import com.alibaba.nacos.common.utils.IoUtils;
import com.rapidark.cloud.platform.gateway.example.feign.OpenFeignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

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

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 提供外部调用API（结合HttpClient示例演示，http://127.0.0.1:8771/route/userCenter/http/getUser）
     * @return
     */
    @RequestMapping("/http/getUser")
    public String getHttpUser(){
        ServiceInstance serviceInstance = loadBalancerClient.choose("provider-examples");
        String url = String.format("http://%s:%d/userCenter/getUser",serviceInstance.getHost(),serviceInstance.getPort());
        log.info("http to {}", url);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        try {
            CloseableHttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200){
                return EntityUtils.toString(response.getEntity());
            }
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(client);
        }
        return null;
    }

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
