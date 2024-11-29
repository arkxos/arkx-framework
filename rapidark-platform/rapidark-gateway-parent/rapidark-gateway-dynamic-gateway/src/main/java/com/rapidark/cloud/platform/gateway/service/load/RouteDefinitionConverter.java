package com.rapidark.cloud.platform.gateway.service.load;

import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.framework.util.RouteConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.*;

public class RouteDefinitionConverter {

	/**
	 * 封装网关路由参数，返回RouteDefinition
	 * @param r
	 * @return
	 */
	public static RouteDefinition converteFrom(GatewayAppRoute r){
		RouteDefinition definition = new RouteDefinition();
		definition.setId(r.getId());
		definition.setOrder(0);

		List<PredicateDefinition> predicates = new ArrayList<>();
		definition.setPredicates(predicates);

		List<FilterDefinition> filters = new ArrayList<>();
		definition.setFilters(filters);

		definition.setUri(ServiceUtil.getURI(r.getUri()));

		//权重（注意此处，当权重值小于等于0，则表示无流量流入，不创建网关）
		if (r.getWeight() != null && r.getWeight() > 0) {
			predicates.add(setPredicateDefinition(RouteConstants.WEIGHT, r.getWeightName(), String.valueOf(r.getWeight())));
		}
		//断言路径
		if (StringUtils.isNotBlank(r.getPath())) {
			predicates.add(setPredicateDefinition(RouteConstants.PATH, r.getPath()));
		}
		//请求模式
		if (StringUtils.isNotBlank(r.getMethod())) {
			predicates.add(setPredicateDefinition(RouteConstants.METHOD, r.getMethod()));
		}
		//断言主机
		if (StringUtils.isNotBlank(r.getHost())) {
			String[] parameters = r.getHost().split(Constants.SEPARATOR_SIGN);
			predicates.add(setPredicateDefinition(RouteConstants.HOST, parameters));
		}
		//断言远程地址
		if (StringUtils.isNotBlank(r.getRemoteAddr())) {
			String[] parameters = r.getRemoteAddr().split(Constants.SEPARATOR_SIGN);
			predicates.add(setPredicateDefinition(RouteConstants.REMOTE_ADDR, parameters));
		}
		//断言Header
		if (StringUtils.isNotBlank(r.getHeader())) {
			String[] parameters = r.getHeader().split(Constants.SEPARATOR_SIGN);
			predicates.add(setPredicateDefinition(RouteConstants.HEADER, parameters));
		}
		//URL截取方式
		if (r.getStripPrefix() != null && r.getStripPrefix() > 0) {
			filters.add(setFilterDefinition(RouteConstants.STRIP_PREFIX, String.valueOf(r.getStripPrefix())));
		}
		//请求参数
		if (StringUtils.isNotBlank(r.getRequestParameter())) {
			String[] parameters = r.getRequestParameter().split(Constants.SEPARATOR_SIGN);
			filters.add(setFilterDefinition(RouteConstants.ADD_REQUEST_PARAMETER, parameters));
		}
		//重写Path路径
		if (StringUtils.isNotBlank(r.getRewritePath())) {
			String[] parameters = r.getRewritePath().split(Constants.SEPARATOR_SIGN);
			filters.add(setFilterDefinition(RouteConstants.REWRITE_PATH, parameters));
		}
		//鉴权
		if (StringUtils.isNotBlank(r.getFilterAuthorizeName())){
			filters.add(setFilterDefinition(RouteConstants.AUTHORIZE, RouteConstants.TRUE));
		}
		//过滤器,id,ip,token
		if (StringUtils.isNotBlank(r.getFilterGatewayName())) {
			String names = r.getFilterGatewayName();
			if (names.contains(RouteConstants.IP)) {
				filters.add(setFilterDefinition(RouteConstants.Secure.SECURE_IP, RouteConstants.TRUE));
			}
			if (names.contains(RouteConstants.ID)) {
				filters.add(setFilterDefinition(RouteConstants.Secure.SECURE_CLIENT_ID, RouteConstants.TRUE));
			}
			if (names.contains(RouteConstants.TOKEN)) {
				filters.add(setFilterDefinition(RouteConstants.Secure.SECURE_TOKEN, RouteConstants.TRUE));
			}
		}
		//缓存策略
		if (r.getCacheTtl() != null && r.getCacheTtl() > 0) {
			Map<String, String> args = new LinkedHashMap<>();
			args.put(RouteConstants.TTL, String.valueOf(r.getCacheTtl()));
			filters.add(setFilterDefinition(RouteConstants.Secure.CACHE_RESULT, args));
		}
		return definition;
	}

	/**
	 * 封装Predicate属性值，返回PredicateDefinition对象
	 * @param name
	 * @param values
	 * @return
	 */
	private static PredicateDefinition setPredicateDefinition(String name, String ... values){
		PredicateDefinition predicate = new PredicateDefinition();
		Map<String, String> args = new HashMap<String, String>();
		predicate.setName(name);
		int i=0;
		for (String value : values){
			args.put(RouteConstants._GENKEY_ + i, value);
			i++;
		}
		predicate.setArgs(args);
		return predicate;
	}

	/**
	 * 封装Filter属性值，返回FilterDefinition对象
	 * @param name
	 * @param args
	 * @return
	 */
	private static FilterDefinition setFilterDefinition(String name, Map<String,String> args){
		FilterDefinition filter = new FilterDefinition();
		filter.setName(name);
		filter.setArgs(args);
		return filter;
	}

	/**
	 * 封装Filter属性值，返回FilterDefinition对象
	 * @param name
	 * @param values
	 * @return
	 */
	private static FilterDefinition setFilterDefinition(String name, String ... values){
		Map<String, String> args = new LinkedHashMap<>();
		int i=0;
		for (String value : values){
			args.put(RouteConstants._GENKEY_ + i, value);
			i++;
		}
		return setFilterDefinition(name, args);
	}

}
