package com.flying.fish.gateway.component.groovy;

import com.flying.fish.formwork.base.BaseGroovyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

/**
 * @Description 请求参数合法性较验
 * @Author JL
 * @Date 2022/2/21
 * @Version V1.0
 */
public class ParameterGroovyService extends BaseGroovyService {

    private Logger log = LoggerFactory.getLogger("ParameterGroovyService");

    @Override
    public void apply(ServerWebExchange exchange) throws Exception {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = exchange.getRequest().getHeaders();

        //clientIp, routeId, ruleName, extednInfo从继承父类BaseGroovyService中获取
        log.info("客户端IP【{}】访问网关路由【{}】执行GroovySrcipt规则引擎动态脚本组件名称【{}】,扩展参数【{}】", clientIp, routeId, ruleName, extednInfo);

        Map<String,String> valueMap = request.getQueryParams().toSingleValueMap();
        String userId = valueMap.get("userId");
        if (StringUtils.isBlank(userId)){
            throw new IllegalArgumentException("缺少userId参数");
        }
        if (!userId.startsWith("100_")){
            throw new IllegalArgumentException("userId参数格式不对");
        }
    }
}
