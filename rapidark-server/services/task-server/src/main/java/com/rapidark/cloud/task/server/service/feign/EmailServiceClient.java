package com.rapidark.cloud.task.server.service.feign;

import com.rapidark.cloud.msg.client.constatns.MsgConstants;
import com.rapidark.cloud.msg.client.service.IEmailClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author: liuyadu
 * @date: 2019/4/1 12:57
 * @description:
 */
@Component
@FeignClient(value = MsgConstants.MSG_SERVICE)
public interface EmailServiceClient extends IEmailClient {

}
