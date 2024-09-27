package com.rapidark.cloud.gateway.server.exception;

import com.rapidark.common.exception.OpenSignatureException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author liuyadu
 */
public interface ServerSignatureDeniedHandler {
    Mono<Void> handle(ServerWebExchange var1, OpenSignatureException var2);
}