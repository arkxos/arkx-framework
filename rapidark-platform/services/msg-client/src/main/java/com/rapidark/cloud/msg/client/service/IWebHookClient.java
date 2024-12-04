package com.rapidark.cloud.msg.client.service;

import com.rapidark.framework.common.model.ResponseResult;
import com.rapidark.cloud.msg.client.model.WebHookMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 推送通知
 *
 * @author woodev
 */
public interface IWebHookClient {
    /**
     * Webhook异步通知
     *
     * @param message
     * @return
     */
    @Schema(title = "Webhook异步通知")
    @PostMapping("/webhook")
    ResponseResult<String> send(
            @RequestBody WebHookMessage message
    ) throws Exception;
}
