package com.rapidark.cloud.gateway.server.exception;

import com.rapidark.framework.common.exception.OpenSignatureException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *
 * @author darkness
 * @date 2022/5/14 17:31
 * @version 1.0
 */
public interface ServerSignatureDeniedHandler {

    Mono<Void> handle(ServerWebExchange var1, OpenSignatureException var2);

}