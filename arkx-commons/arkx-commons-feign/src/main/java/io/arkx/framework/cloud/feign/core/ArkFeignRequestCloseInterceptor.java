package io.arkx.framework.cloud.feign.core;

import org.springframework.http.HttpHeaders;

import feign.RequestInterceptor;

/**
 * @author lengleng
 * @date 2024/3/15
 *       <p>
 *       http connection close
 */
public class ArkFeignRequestCloseInterceptor implements RequestInterceptor {

    /**
     * set connection close
     *
     * @param template
     */
    @Override
    public void apply(feign.RequestTemplate template) {
        template.header(HttpHeaders.CONNECTION, "close");
    }

}
