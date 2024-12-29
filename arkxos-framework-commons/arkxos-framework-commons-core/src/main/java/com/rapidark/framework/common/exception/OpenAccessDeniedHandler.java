package com.rapidark.framework.common.exception;

import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.framework.common.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 自定义访问拒绝
 *
 * @author liuyadu
 */
@Slf4j
public class OpenAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) {
        ResponseResult responseResult = OpenGlobalExceptionHandler.resolveException(exception, request.getRequestURI());
        response.setStatus(responseResult.getHttpStatus());
        WebUtils.writeJson(response, responseResult);
    }
}
