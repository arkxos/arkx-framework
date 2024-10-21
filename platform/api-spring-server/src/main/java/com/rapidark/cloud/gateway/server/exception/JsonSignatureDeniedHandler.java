package com.rapidark.cloud.gateway.server.exception;

import com.alibaba.fastjson.JSONObject;
import com.rapidark.framework.commons.exception.OpenGlobalExceptionHandler;
import com.rapidark.framework.commons.exception.OpenSignatureException;
import com.rapidark.framework.commons.model.ResultBody;
import com.rapidark.cloud.gateway.server.service.AccessLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * 网关验签异常处理,记录日志
 * @author darkness
 * @date 2022/5/14 17:31
 * @version 1.0
 */
@Slf4j
public class JsonSignatureDeniedHandler implements ServerSignatureDeniedHandler {

    private AccessLogService accessLogService;

    public JsonSignatureDeniedHandler(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, OpenSignatureException e) {
        ResultBody resultBody = OpenGlobalExceptionHandler.resolveException(e, exchange.getRequest().getURI().getPath());
        return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap((response) -> {
            response.setStatusCode(HttpStatus.valueOf(resultBody.getHttpStatus()));
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            DataBuffer buffer = dataBufferFactory.wrap(JSONObject.toJSONString(resultBody).getBytes(Charset.defaultCharset()));
            // 保存日志
            accessLogService.sendLog(exchange, e);
            return response.writeWith(Mono.just(buffer)).doOnError((error) -> {
                DataBufferUtils.release(buffer);
            });
        });
    }
}
