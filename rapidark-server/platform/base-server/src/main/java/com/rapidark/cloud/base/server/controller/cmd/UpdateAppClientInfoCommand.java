package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/25 10:50
 */
@Data
@ApiModel(value = "修改应用开发信息")
public class UpdateAppClientInfoCommand {

    @ApiModelProperty(required = true, value = "应用Id")
    @NotNull(message = "应用Id不能为空")
    private String appId;
    @ApiModelProperty(required = true, value = "授权类型(多个使用,号隔开)")
    @NotNull(message = "授权类型不能为空")
    private String grantTypes;
    @ApiModelProperty(required = true, value = "第三方应用授权回调地址")
    @NotNull(message = "第三方应用授权回调地址不能为空")
    private String redirectUrls;
    @ApiModelProperty(required = true, value = "用户授权范围(多个使用,号隔开)")
    @NotNull(message = "用户授权范围不能为空")
    private String scopes;
    @ApiModelProperty(required = false, value = "用户自动授权范围(多个使用,号隔开)")
    private String autoApproveScopes;
    @NotNull(message = "令牌有效期不能为空")
    @ApiModelProperty(required = true, value = "令牌有效期(秒)")
    private Integer accessTokenValidity;
    @NotNull(message = "刷新令牌有效期不能为空")
    @ApiModelProperty(required = true, value = "刷新令牌有效期(秒)")
    private Integer refreshTokenValidity;

}
