package io.arkx.framework.performance.monitor.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.arkx.framework.performance.monitor.TraceContext;

/**
 * @author Nobody
 * @date 2025-06-18 23:07
 * @since 1.0
 */
@Aspect
@Component
public class StreamMonitoringAspect {

    @Autowired
    private TraceContext traceContext;

    // 拦截所有流操作方法
    @Pointcut("call(* java.util.stream.Stream.*(..)) && !within(io.arkx.framework.performance.monitor..*)")
    public void streamOperation() {
    }

    @Around("streamOperation()")
    public Object aroundStreamOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // 开始流上下文
        traceContext.startStreamProcessing();

        try {
            return joinPoint.proceed();
        } finally {
            // 结束流上下文
            traceContext.endStreamProcessing();
        }
    }

}
