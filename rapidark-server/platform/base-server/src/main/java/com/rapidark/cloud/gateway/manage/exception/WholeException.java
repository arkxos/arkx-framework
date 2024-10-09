package com.rapidark.cloud.gateway.manage.exception;

import com.rapidark.common.model.ResultBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 捕获全局异常
 * @author darkness
 * @date 2022/6/20 11:53
 * @version 1.0
 */
@Slf4j
@Order(1)
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class WholeException {

    static{
        log.info("加载全局异常捕获类" );
    }

    /**
     * 返回异常包装信息
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class})
    public Object exceptionHandler(Exception e){
        log.error("error:",e);
        ResultBody result = ResultBody.failed();
        result.setMessage(e.getMessage());
        return result;
    }
}
