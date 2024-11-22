package com.rapidark.cloud.platform.gateway.handle;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSONObject;
import com.rapidark.cloud.platform.gateway.framework.util.ApiResult;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

/**
 * @Description 自定义对限流组件的BlockException异常事件的消息重新包装的处理器（可用于自定义统一格式输出）
 * @Author JL
 * @Date 2022/10/31
 * @Version V1.0
 */
public class CustomBlockRequestHandler implements BlockRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomBlockRequestHandler.class);
    private static final String DEFAULT_BLOCK_MSG_PREFIX = "Blocked by Sentinel, ";

    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable ex) {
        final String msg ;
        if (ex instanceof FlowException) {
            msg = "提示：服务流控规则已启用，请求触发限流机制，请联系运维人员处理。此消息由网关服务返回！";
            // 注意Setinel目前熔断只支持RT慢请求降级，对于异常比例和异常数降级暂时无支持，参见：https://github.com/alibaba/Sentinel/issues/1842
        } else if (ex instanceof DegradeException) {
            msg = "提示：服务熔断规则已启用，请求触发熔断机制，请联系运维人员处理。此消息由网关服务返回！";
        } else if (ex instanceof AuthorityException) {
            msg = "提示：服务授权模式已启用，请求没有权限访问，请联系运维人员处理。此消息由网关服务返回！";
        } else if (ex instanceof SystemBlockException) {
            msg = "提示：服务系统规则已启用，请求超出系统阈值，请联系运维人员处理。此消息由网关服务返回！";
        } else if (ex instanceof ParamFlowException){
            msg = "提示：服务热点规则已启用，请求超出热点资源阈值，请联系运维人员处理。此消息由网关服务返回！";
        } else {
            msg = ex.getMessage();
        }
        log.error("handleRequest throwable. msg:" + msg , ex);
        //Throwable throwable = new Throwable(msg ,ex);
        // JSON result by default.
        String jsonMsg = JSONObject.toJSONString(new ApiResult(Constants.FAILED, DEFAULT_BLOCK_MSG_PREFIX + msg, null));
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .body(fromValue(jsonMsg));
    }
}
