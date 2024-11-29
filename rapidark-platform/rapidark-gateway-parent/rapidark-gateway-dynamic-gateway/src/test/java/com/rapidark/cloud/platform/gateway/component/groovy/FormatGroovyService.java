package com.rapidark.cloud.platform.gateway.component.groovy;

import com.alibaba.fastjson.JSONObject;
import com.rapidark.cloud.platform.gateway.framework.base.BaseGroovyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 服务端响应数据格式化包装
 * @Author JL
 * @Date 2022/3/7
 * @Version V1.0
 */
public class FormatGroovyService  extends BaseGroovyService {

	private Logger log = LoggerFactory.getLogger("FormatGroovyService");

	@Override
	public void apply(ServerWebExchange exchange) throws Exception {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		HttpHeaders headers = exchange.getRequest().getHeaders();
		HttpStatusCode httpStatus = response.getStatusCode();
		MediaType mediaType = headers.getContentType();

		//clientIp, routeId, ruleName, extednInfo从继承父类BaseGroovyService中获取
		log.info("客户端IP【{}】访问网关路由【{}】执行GroovySrcipt规则引擎动态脚本组件名称【{}】,扩展参数【{}】", clientIp, routeId, ruleName, extednInfo);

		if (mediaType != MediaType.APPLICATION_JSON){
			System.out.println(body);
			Map<String, String> dataMap = new HashMap<>();
			dataMap.put("code", "0");
			dataMap.put("data", body);
			setBody(JSONObject.toJSONString(dataMap));
		}
	}
}

