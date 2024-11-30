package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/25 10:50
 */
@Data
@Schema(description = "修改应用开发信息")
public class UpdateAppClientInfoCommand {

    @Schema(required = true, title = "应用Id")
    @NotNull(message = "应用Id不能为空")
    private String appId;
    @Schema(required = true, title = "授权类型(多个使用,号隔开)")
    @NotNull(message = "授权类型不能为空")
    private String grantTypes;
    @Schema(required = true, title = "第三方应用授权回调地址")
    @NotNull(message = "第三方应用授权回调地址不能为空")
    private String redirectUrls;
    @Schema(required = true, title = "用户授权范围(多个使用,号隔开)")
    @NotNull(message = "用户授权范围不能为空")
    private String scopes;
    @Schema(required = false, title = "用户自动授权范围(多个使用,号隔开)")
    private String autoApproveScopes;
    @NotNull(message = "令牌有效期不能为空")
    @Schema(required = true, title = "令牌有效期(秒)")
    private Integer accessTokenValidity;
    @NotNull(message = "刷新令牌有效期不能为空")
    @Schema(required = true, title = "刷新令牌有效期(秒)")
    private Integer refreshTokenValidity;

}
