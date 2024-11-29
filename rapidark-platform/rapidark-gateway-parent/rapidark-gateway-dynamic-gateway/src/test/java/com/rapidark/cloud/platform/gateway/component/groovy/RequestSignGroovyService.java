package com.rapidark.cloud.platform.gateway.component.groovy;

import com.alibaba.fastjson.JSONObject;
import com.rapidark.cloud.platform.gateway.framework.base.BaseGroovyService;
import com.rapidark.cloud.platform.gateway.framework.util.Md5Utils;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RequestSignGroovyService extends BaseGroovyService {

	@Override
	public void apply(ServerWebExchange exchange) {
		ServerHttpRequest request = exchange.getRequest();
		HttpHeaders headers = exchange.getRequest().getHeaders();
		//clientIp, routeId, ruleName, extednInfo从继承父类BaseGroovyService中获取
		log.info("客户端IP【{}】访问网关路由【{}】执行GroovySrcipt规则引擎动态脚本组件名称【{}】,扩展参数【{}】", clientIp, routeId, ruleName, extednInfo);

		long userId = Long.parseLong(paramMap.get("userId"));
		JSONObject jsonObject = JSONObject.parseObject(extednInfo);
		String secretKey = jsonObject.getString("secretKey");
		String sign = Md5Utils.getMD5(userId + System.currentTimeMillis() + secretKey, StandardCharsets.UTF_8.toString());
		paramMap.put("sign", sign);
	}

}