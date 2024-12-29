package com.arkxit.cloud.platform.common.feign.sentinel.handle;

import com.rapidark.framework.common.model.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@Order(10000)
@RestControllerAdvice
public class GlobalWebExceptionHandler {

	/**
	 * 保持和低版本请求路径不存在的行为一致
	 * <p>
	 * <a href="https://github.com/spring-projects/spring-boot/issues/38733">[Spring Boot
	 * 3.2.0] 404 Not Found behavior #38733</a>
	 * @param exception
	 * @return ResponseResult
	 */
	@ExceptionHandler({ NoResourceFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseResult notFoundExceptionHandler(NoResourceFoundException exception) {
		log.debug("请求路径 404 {}", exception.getMessage());
		return ResponseResult.failed(exception.getMessage());
	}

}
