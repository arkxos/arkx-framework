package com.rapidark.cloud.gateway.server.exception;

import com.rapidark.common.exception.OpenCryptoException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *
 * @author darkness
 * @date 2022/5/14 17:28
 * @version 1.0
 */
public interface CryptoExceptionHandler {

    Mono<Void> handle(ServerWebExchange var1, OpenCryptoException var2);

}