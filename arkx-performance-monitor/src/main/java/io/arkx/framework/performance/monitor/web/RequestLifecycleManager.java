package io.arkx.framework.performance.monitor.web;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.arkx.framework.performance.monitor.TraceContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Nobody
 * @date 2025-06-06 1:04
 * @since 1.0
 */
// 请求初始化和清理
@Component
public class RequestLifecycleManager {

    private final TraceContext traceContext;

    @Autowired
    public RequestLifecycleManager(TraceContext traceContext) {
        this.traceContext = traceContext;
    }

    public void startRequest(HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        request.setAttribute("REQUEST_ID", requestId);
        traceContext.startRequest(requestId);
    }

    public void endRequest(HttpServletRequest request) {
        traceContext.endRequest();
    }

}
