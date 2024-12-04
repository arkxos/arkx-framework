package com.rapidark.cloud.msg.client.service;

import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.cloud.msg.client.model.SmsMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 推送通知
 *
 * @author woodev
 */
public interface ISmsClient {
    /**
     * 短信通知
     *
     * @param message
     * @return
     */
    @Schema(title = "发送短信")
    @PostMapping(value = "/sms")
    ResponseResult<String> send(@RequestBody SmsMessage message);

    /**
     * feign内部调用
     *
     * @param message
     * @return
     */
    @PostMapping(value = "/sms/feign", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseResult feignSendSms(SmsMessage message);
}
