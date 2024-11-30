package com.rapidark.cloud.task.server.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rapidark.cloud.base.client.constants.BaseConstants;
import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import com.rapidark.cloud.task.server.service.feign.GatewayServiceClient;
import com.rapidark.framework.common.model.ResultBody;
import com.rapidark.framework.common.security.http.OpenRestTemplate;
import com.rapidark.framework.common.utils.RedisUtils;
import com.rapidark.framework.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 微服务远程调用任务
 *
 * @author liuyadu
 */
@Slf4j
public class HttpExecuteJob implements Job {
    @Autowired
    private OpenRestTemplate openRestTemplate;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 负载均衡
     */
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private GatewayServiceClient gatewayServiceClient;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws RuntimeException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String serviceId = dataMap.getString("serviceId");
        String method = dataMap.getString("method");
        method = StringUtils.isBlank(method) ? "POST" : method;
        String path = dataMap.getString("path");
        String contentType = dataMap.getString("contentType");
        contentType = StringUtils.isBlank(contentType) ? MediaType.APPLICATION_FORM_URLENCODED_VALUE : contentType;
        String body = dataMap.getString("body");
        String url = getUrlByRoute(serviceId, path);
        HttpHeaders headers = new HttpHeaders();
        HttpMethod httpMethod = HttpMethod.resolve(method.toUpperCase());
        HttpEntity requestEntity = null;
        headers.setContentType(MediaType.parseMediaType(contentType));
        if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            // json格式
            requestEntity = new HttpEntity(body, headers);
        } else {
            // 表单形式
            // 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
            MultiValueMap<String, String> params = new LinkedMultiValueMap();
            if (StringUtils.isNotBlank(body)) {
                Map data = JSONObject.parseObject(body, Map.class);
                params.putAll(data);
                requestEntity = new HttpEntity(params, headers);
            }
        }
        log.debug("==> url[{}] method[{}] data=[{}]", url, httpMethod, requestEntity);
        ResponseEntity<String> result = openRestTemplate.exchange(url, httpMethod, requestEntity, String.class);
        System.out.println(result.getBody());
    }

    private String getUrlByRoute(String name, String path) {
        List<GatewayAppRoute> routes = getApiRouteList();
        for (GatewayAppRoute route : routes) {
            if (route.getSystemCode().equals(name)) {
                if (BaseConstants.ROUTE_TYPE_URL.equalsIgnoreCase(route.getType())) {
                    if (route.getUri().endsWith("/")) {
                        return route.getUri() + path.replaceFirst("/", "");
                    }
                    return route.getUri() + path;
                } else if (BaseConstants.ROUTE_TYPE_SERVICE.equalsIgnoreCase(route.getType())) {
                    ServiceInstance serviceInstance = loadBalancerClient.choose(name);
                    // 获取服务实例
                    if (serviceInstance == null) {
                        throw new RuntimeException(String.format("%s服务暂不可用", name));
                    }
                    return String.format("%s%s", serviceInstance.getUri(), path);
                }
            }
        }
        throw new RuntimeException(String.format("%s服务暂不可用", name));
    }

    public List<GatewayAppRoute> getApiRouteList() {
        List<String> routeJsonList = redisUtils.getList(BaseConstants.ROUTE_LIST_CACHE_KEY);
        if (routeJsonList.isEmpty()) {
            ResultBody<List<GatewayAppRoute>> resultBody = gatewayServiceClient.getApiRouteList();
            List<GatewayAppRoute> routes = resultBody.getData();
            if (!routes.isEmpty()) {
                List<String> jsonList = convertToJson(routes);
                routeJsonList = jsonList;
                redisUtils.setList(BaseConstants.ROUTE_LIST_CACHE_KEY, jsonList, BaseConstants.ROUTE_LIST_CACHE_TIME);
            }
        }
        List<GatewayAppRoute> routes = convertFromJson(routeJsonList);
        return routes;
    }

    private List<String> convertToJson(List<GatewayAppRoute> routes) {
        List<String> result = new ArrayList<>();
        for (GatewayAppRoute route : routes) {
            result.add(JSON.toJSONString(route));
        }
        return result;
    }

    private List<GatewayAppRoute> convertFromJson(List<String> routes) {
        List<GatewayAppRoute> result = new ArrayList<>();
        for (String route : routes) {
            result.add(JSON.parseObject(route, GatewayAppRoute.class));
        }
        return result;
    }
}
