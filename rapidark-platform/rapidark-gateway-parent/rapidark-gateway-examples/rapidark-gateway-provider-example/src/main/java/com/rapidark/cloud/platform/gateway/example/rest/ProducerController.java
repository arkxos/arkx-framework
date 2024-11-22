package com.rapidark.cloud.platform.gateway.example.rest;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

/**
 * @Description 生产者控制器
 * @Author JL
 * @Date 2020/04/23
 * @Version V1.0
 */
@Slf4j
@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Value("${server.port}")
    private String port;

    @Autowired
    private ServerCodecConfigurer serverCodecConfigurer ;

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/info")
    public String info() {
        log.info("info api server port: " + port + ",ok:" + System.currentTimeMillis());
        return "info api server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/test")
    public String test() {
        log.info("test api server port: " + port + ",ok:" + System.currentTimeMillis());
        return "test api server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/test0")
    public String test0() {
        log.info("test0 api server port: " + port + ",ok:" + System.currentTimeMillis());
        return "test0 api server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/test1")
    public String test1() {
        try {
            Thread.sleep(4 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("test1 api server port: " + port + ",ok:" + System.currentTimeMillis());
        return "test1 api server port: " + port + ",ok:" + System.currentTimeMillis();
    }

    /**
     * 提供外部调用API(测试断言：重写路径向RewritePath)
     * @return
     */
    @RequestMapping(value = "/getToken")
    public String getToken() {
//        Assert.isTrue(false, "test error!");
        String uuid = UUID.randomUUID().toString();
        log.info("getToken api server port: " + port + ",ok:" + System.currentTimeMillis() + ", uuid:" + uuid);
        return uuid;
    }

    /**
     * 提供外部调用API
     * @return
     */
    @RequestMapping(value = "/detail")
    public Mono<Void> detail(final ServerHttpRequest request, final ServerHttpResponse response) {
        log.info("request param:" + request.getQueryParams().toString());
        MediaType mediaType = request.getHeaders().getContentType();
        final ResolvableType reqDataType = ResolvableType.forClass(byte[].class);
        return response.writeWith(serverCodecConfigurer.getReaders().stream()
                .filter(reader -> reader.canRead(reqDataType, MediaType.ALL))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Data"))
                .readMono(reqDataType, request, Collections.emptyMap())
                .cast(byte[].class)
                .switchIfEmpty(Mono.justOrEmpty("".getBytes(StandardCharsets.UTF_8)))
                .map(bytes -> {
                    // TODO:  实现自己的业务
                    final NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
                    String result ;
                    if (mediaType != null && MediaType.APPLICATION_JSON_VALUE.equals(mediaType.toString())) {
                        result = "{\"code\":\"0\",\"data\":\"test0 api server port: " + port + ",ok:" + System.currentTimeMillis() + "\"}";
                    } else {
                        result = "test0 api server port: " + port + ",ok:" + System.currentTimeMillis();
                    }
                    try {
                        final String reqBody = new String(bytes, StandardCharsets.UTF_8);
                        log.info("request body:" + reqBody);
                        log.info("response body:"+ result);
                        return nettyDataBufferFactory.wrap(result.getBytes(StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return nettyDataBufferFactory.wrap(e.getMessage().getBytes(StandardCharsets.UTF_8));
                    }
                }));
    }
}
