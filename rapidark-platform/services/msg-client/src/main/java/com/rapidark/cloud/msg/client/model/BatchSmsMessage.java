package com.rapidark.cloud.msg.client.model;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: linrongxin
 * @Date: 2019/9/3 14:38
 */
@Schema(title = "批量短信消息")
@Data
@EqualsAndHashCode(callSuper = false)
public class BatchSmsMessage extends BaseMessage {
    @Schema(required = true, name = "phoneNumberJson", title = "接收短信的手机号码，JSON数组格式", example = "[\"15900000000\",\"13500000000\"]")
    private String phoneNumberJson;

    @Schema(required = true, name = "signNameJson", title = "短信签名名称，JSON数组格式", example = "[\"跨境知道\",\"跨境知道\"]")
    private String signNameJson;

    @Schema(required = true, name = "templateCode", title = "短信模板CODE", example = "SMS_173348704")
    private String templateCode;

    @Schema(required = true, name = "templateParamJson", title = "短信模板变量对应的实际值，JSON格式", example = "[{\"name\":\"用户1\",\"live_name\":\"直播名称1\",\"time\":\"2019-09-03\"},{\"name\":\"用户2\",\"live_name\":\"直播名称2\",\"time\":\"2019-09-04\"}]")
    private String templateParamJson;
}
