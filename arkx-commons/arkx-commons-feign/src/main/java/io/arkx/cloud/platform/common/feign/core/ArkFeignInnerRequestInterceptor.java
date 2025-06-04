package io.arkx.cloud.platform.common.feign.core;

import java.lang.reflect.Method;

import org.springframework.core.Ordered;

import io.arkx.cloud.platform.common.feign.annotation.NoToken;
import io.arkx.common.core.constant.SecurityConstants;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author lengleng
 * @date 2024/6/1
 */
public class ArkFeignInnerRequestInterceptor implements RequestInterceptor, Ordered {

	/**
	 * Called for every request. Add data using methods on the supplied
	 * {@link RequestTemplate}.
	 * @param template
	 */
	@Override
	public void apply(RequestTemplate template) {
		Method method = template.methodMetadata().method();
		NoToken noToken = method.getAnnotation(NoToken.class);
		if (noToken != null) {
			template.header(SecurityConstants.FROM, SecurityConstants.FROM_IN);
		}
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}

}
