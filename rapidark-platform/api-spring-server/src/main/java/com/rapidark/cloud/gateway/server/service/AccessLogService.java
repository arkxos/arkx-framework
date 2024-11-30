package com.rapidark.cloud.gateway.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.rapidark.cloud.base.client.model.entity.GatewayAccessLogs;
import com.rapidark.cloud.gateway.server.util.ReactiveWebUtils;
import com.rapidark.framework.common.constants.QueueConstants;
import com.rapidark.framework.common.security.OpenUserDetails;
import com.rapidark.cloud.gateway.server.filter.context.GatewayContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 *
 * @author darkness
 * @date 2022/5/14 17:39
 * @version 1.0
 */
@Slf4j
@Component
public class AccessLogService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${spring.application.name}")
    private String defaultServiceId;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @JsonIgnore
    private Set<String> ignores = new HashSet<>(Arrays.asList(new String[]{
            "/**/oauth/check_token/**",
            "/**/gateway/access/logs/**",
            "/webjars/**"
    }));

    /**
     * 不记录日志
     *
     * @param requestPath
     * @return
     */
    private boolean ignore(String requestPath) {
        Iterator<String> iterator = ignores.iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            if (antPathMatcher.match(path, requestPath)) {
                return true;
            }
        }
        return false;
    }

    @Async
    public void sendLog(ServerWebExchange exchange, Exception ex) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        try {
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            int httpStatus = response.getStatusCode().value();
            String requestPath = request.getURI().getPath();
            String method = request.getMethodValue();
            Map<String, String> headers = request.getHeaders().toSingleValueMap();
            Map<String, String> data = Maps.newHashMap();
            GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
            String responseBody = "";
            String bizId = "";
            Integer bizStatus = -1;
            String error = null;
            JSONObject responseBodyJsonObject = null;
            if (gatewayContext != null) {
                data = gatewayContext.getAllRequestData().toSingleValueMap();
                responseBody = gatewayContext.getResponseBody();
                if(responseBody != null) {
                    responseBodyJsonObject = JSON.parseObject(responseBody);
                    bizId = responseBodyJsonObject.getString("bizId");
                }

                if(StringUtils.isEmpty(bizId)) {
                    bizId = data.get("bizId");
                }
                if(StringUtils.isEmpty(bizId)) {
                    bizId = data.get("handlingId");
                }
                if(StringUtils.isEmpty(bizId)) {
                    bizId = data.get("id");
                }
                if(StringUtils.isEmpty(bizId)) {
                    bizId = data.get("protocolId");
                }
                if(StringUtils.isEmpty(bizId) && responseBodyJsonObject != null) {
                    bizId = responseBodyJsonObject.getString("id");
                }

                if(responseBodyJsonObject != null) {
                    bizStatus = responseBodyJsonObject.getInteger("code");
                }

                if (bizStatus != null && bizStatus != 0 && responseBodyJsonObject != null) {
                    error = responseBodyJsonObject.getString("message");
                }
            }
            String serviceId = null;
            if (route != null) {
                serviceId = route.getUri().toString().replace("lb://", "");
            }
            String ip = ReactiveWebUtils.getRemoteAddress(exchange);
            String userAgent = headers.get(HttpHeaders.USER_AGENT);
            Date requestTime = exchange.getAttribute("requestTime");

            if (ex != null) {
                error = ex.getMessage();
            }
            if (ignore(requestPath)) {
                return;
            }
            GatewayAccessLogs gatewayAccessLogs = new GatewayAccessLogs();
            gatewayAccessLogs.setRequestTime(requestTime);
            gatewayAccessLogs.setServiceId(serviceId == null ? defaultServiceId : serviceId);
            gatewayAccessLogs.setHttpStatus(httpStatus+"");
            gatewayAccessLogs.setHeaders(JSONObject.toJSONString(headers));
            gatewayAccessLogs.setBizId(bizId);
            gatewayAccessLogs.setBizStatus(bizStatus);
            gatewayAccessLogs.setPath(requestPath);
            gatewayAccessLogs.setParams(JSONObject.toJSONString(data));
            gatewayAccessLogs.setIp(ip);
            gatewayAccessLogs.setMethod(method);
            gatewayAccessLogs.setUserAgent(userAgent);
            gatewayAccessLogs.setResponseTime(new Date());
            gatewayAccessLogs.setError(error);
            if (bizStatus != null && bizStatus != 0) {
                gatewayAccessLogs.setResponseBody(responseBody);
            }

            Mono<Authentication> authenticationMono = exchange.getPrincipal();
            Mono<Object> authentication = authenticationMono
                    .filter(Authentication::isAuthenticated)
                    .map(Authentication::getPrincipal);

//                    .cast(OpenUserDetails.class);
            authentication.subscribe(principal -> {
                if (principal != null) {
                    if (principal instanceof OpenUserDetails) {
                        gatewayAccessLogs.setAuthentication(JSONObject.toJSONString(principal));
                    } else if (principal instanceof String) {
                        gatewayAccessLogs.setAuthentication(principal+"");
                    } else {
                        gatewayAccessLogs.setAuthentication(principal+"");
                    }
                }
            });
            amqpTemplate.convertAndSend(QueueConstants.QUEUE_ACCESS_LOGS, JSONObject.toJSONString(gatewayAccessLogs));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("access logs save error:{}", e.getMessage());
        }
    }
}
