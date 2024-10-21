package com.rapidark.cloud.gateway.server.fallback;

import com.rapidark.framework.common.constants.ErrorCode;
import com.rapidark.framework.common.model.ResultBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 响应超时熔断处理器
 * @author darkness
 * @date 2022/5/14 17:31
 * @version 1.0
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public Mono<ResultBody> fallback() {
        return Mono.just(ResultBody.failed().code(ErrorCode.GATEWAY_TIMEOUT.getCode()).msg("访问超时，请稍后再试!"));
    }
    
}
