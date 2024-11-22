package com.rapidark.cloud.platform.gateway.example.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.*;

import static org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer.REQUEST;

/**
 * @Description 自定义LoadBalancerClient客户端实现类，当前类复制BlockingLoadBalancerClient类代码，对choose()方法进行改造，
 * 以解决原代码中响应式编程中的同步阻塞问题（理论上不因该存在此问题，具体原因未查明，此替换暂为解决方案），
 * 导致@LoadBalancer后无法识别注册中心的服务别名，feign调用失败；
 * @Date 2022/12/23
 * @Version V1.0
 */
@Primary
@Service("loadBalancerClient")
public class CustomBlockingLoadBalancerClient extends BlockingLoadBalancerClient {

    private final ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory;

    public CustomBlockingLoadBalancerClient(ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory) {
        super(loadBalancerClientFactory);
        this.loadBalancerClientFactory = loadBalancerClientFactory;
    }

    @Override
    public <T> ServiceInstance choose(String serviceId, Request<T> request) {
        ReactiveLoadBalancer<ServiceInstance> loadBalancer = loadBalancerClientFactory.getInstance(serviceId);
        if (loadBalancer == null) {
            return null;
        }

        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        Executors.defaultThreadFactory());
        try {
            // WebFlux异步调用，同步会报错
            Future<Response<ServiceInstance>> future = executorService.submit(() -> Mono.from(loadBalancer.choose(request)).block());
            Response<ServiceInstance> loadBalancerResponse = future.get();
            if (loadBalancerResponse == null) {
                return null;
            }
            return loadBalancerResponse.getServer();
        } catch(Exception e){
            e.printStackTrace();
            return null;
        } finally {
            executorService.shutdown();
        }

        //  Mono.from(x).block()此处存在同步阻塞问题，导致抛异常; 采用下面的新建线程调用方案；
//        Response<ServiceInstance> loadBalancerResponse = Mono.from(loadBalancer.choose(request)).block();
//        if (loadBalancerResponse == null) {
//            return null;
//        }
//        return loadBalancerResponse.getServer();
    }

}
