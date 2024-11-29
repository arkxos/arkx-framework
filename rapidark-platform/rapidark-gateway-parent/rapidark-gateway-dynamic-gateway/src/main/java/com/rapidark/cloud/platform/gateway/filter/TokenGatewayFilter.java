package com.rapidark.cloud.platform.gateway.filter;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.rapidark.cloud.platform.gateway.cache.RegServerCache;
import com.rapidark.cloud.platform.gateway.vo.GatewayRegServer;
import com.rapidark.cloud.platform.gateway.framework.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.rapidark.cloud.platform.gateway.support.CustomGatewayToStringStyler.filterToStringCreator;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @Description 验证用户是否携带token(token的有效性与正确性,如果服务端验证token,请不要开启此token验证过滤器，避免解析方式不一致被拦截)
 * @Author JL
 * @Date 2020/05/19
 * @Version V1.0
 */
@Slf4j
public class TokenGatewayFilter implements GatewayFilter, Ordered {

	private boolean enabled;

	public TokenGatewayFilter(boolean enabled){
		this.enabled = enabled ;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (!enabled) {
			return chain.filter(exchange);
		}

		Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
		String routeId = RouteUtils.getBalancedToRouteId(route.getId());

		//做了负载均衡的route服务不做客户端Token验证
//		if (routeId.startsWith(RouteConstants.BALANCED)){
//			return chain.filter(exchange);
//		}
		String ip = NetworkIpUtils.getIpAddress(exchange.getRequest());
		String token = getToken(exchange.getRequest());
		if (StringUtils.isBlank(token)){
			String msg = "客户端Token值为空，无权限访问网关路由："+ routeId +"! Ip:" + ip;
			log.error(msg);
			return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
		}
		GatewayRegServer regServer = getCacheRegServer(token, routeId);
		if (regServer == null){
			String msg = "客户端Token未注册使用，无权限访问网关路由："+ routeId +"! Ip:" + ip;
			log.error(msg);
			return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
		}
		try {
			if (isPassToken(regServer.getClientId(), regServer.getToken(), regServer.getSecretKey())) {
				String msg = "客户端Token值验证未通过，无权限访问网关路由：" + routeId + "! Ip:" + ip;
				log.error(msg);
				return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
			}
			//会话超时异常
		}catch(TokenExpiredException tee){
			String msg = "客户端Token值会话超时，无权限访问网关路由：" + routeId + "! Ip:" + ip;
			log.error(msg, tee);
			return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
			//验签无效异常
		}catch(SignatureVerificationException sve){
			String msg = "客户端Token值验签无效，无权限访问网关路由：" + routeId + "! Ip:" + ip;
			log.error(msg, sve);
			return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
		}catch (Exception e){
			String msg = "客户端Token值有未知异常，无权限访问网关路由：" + routeId + "! Ip:" + ip;
			log.error(msg, e);
			return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
		}
		return chain.filter(exchange);
	}

	/**
	 * 查询和对比缓存中的注册客户端
	 * @param token
	 * @return
	 */
	public GatewayRegServer getCacheRegServer(String token, String routeId){
		List<GatewayRegServer> regServers = RegServerCache.get(routeId);
		if (CollectionUtils.isEmpty(regServers)){
			return null;
		}
		Optional<GatewayRegServer> optional = regServers.stream().filter(r -> token.equals(r.getToken())).findFirst();
		return optional.orElse(null);
	}

	/**
	 * 获取请求header中带的token
	 * @param request
	 * @return
	 */
	public String getToken(ServerHttpRequest request){
		HttpHeaders headers = request.getHeaders();
		//验证是否带token
		String token = request.getQueryParams().getFirst(RouteConstants.TOKEN);
		if (StringUtils.isBlank(token)){
			token = headers.getFirst(RouteConstants.TOKEN);
		}
		return token;
	}

	/**
	 * 是否允许通行客户端Token
	 * @param token
	 * @return
	 */
	public boolean isPassToken(String clientId, String token, String secretKey) throws TokenExpiredException, SignatureVerificationException{
		String sub = JwtTokenUtils.parseToken(token, secretKey);
		if (sub != null && sub.contains(",")){
			//subs=routeId,clientId,time
			String [] subs = sub.split(",");
			//比较缓存中的clientId是否与请求中token所带的clinetId一致，防止盗用其它注册客户端token
			return !clientId.equals(subs[1]);
		}
		return false;
	}

	@Override
	public String toString() {
		return filterToStringCreator(TokenGatewayFilter.this)
				.append("enabled", true)
				.append("order", getOrder())
				.toString();
	}

	@Override
	public int getOrder() {
		return 3;
	}
}

