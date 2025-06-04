package io.arkx.framework.common.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import io.arkx.framework.common.model.ResponseResult;
import io.arkx.framework.common.utils.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义未认证处理
 *
 * @author liuyadu
 */
@Slf4j
public class OpenAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        ResponseResult responseResult = OpenGlobalExceptionHandler.resolveException(exception, request.getRequestURI());
        response.setStatus(responseResult.getHttpStatus());
        WebUtils.writeJson(response, responseResult);
    }
}