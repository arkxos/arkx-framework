package com.flying.fish.gateway.component.groovy;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.Md5Utils;
import com.rapidark.cloud.gateway.formwork.base.BaseGroovyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.nio.charset.StandardCharsets;

/**
 * @Description 为请求生成签名，并添加sign鉴权字段
 * @Author JL
 * @Date 2022/2/21
 * @Version V1.0
 */
public class RequestSignGroovyService extends BaseGroovyService {

    private Logger log = LoggerFactory.getLogger("RequestSignGroovyService");

    @Override
    public void apply(ServerWebExchange exchange) throws Exception {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = exchange.getRequest().getHeaders();
        //clientIp, routeId, ruleName, extednInfo从继承父类BaseGroovyService中获取
        log.info("客户端IP【{}】访问网关路由【{}】执行GroovySrcipt规则引擎动态脚本组件名称【{}】,扩展参数【{}】", clientIp, routeId, ruleName, extednInfo);

        String userId = paramMap.get("userId");
        JSONObject jsonObject = JSONObject.parseObject(extednInfo);
        String secretKey = jsonObject.getString("secretKey");
        String sign = Md5Utils.getMD5(userId + System.currentTimeMillis() + secretKey, StandardCharsets.UTF_8.toString());
        paramMap.put("sign", sign);
    }

}