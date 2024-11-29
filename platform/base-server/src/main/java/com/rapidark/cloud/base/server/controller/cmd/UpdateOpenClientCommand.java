package com.rapidark.cloud.base.server.controller.cmd;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/5/30 11:25
 */
@Data
@Schema(description = "修改开放客户端参数")
public class UpdateOpenClientCommand {

    @Schema(required = true, value = "开放客户端Id", example = "DWixDuMagft5uoQXjKgC3m")
    @NotEmpty(message = "开放客户端Id不能为空")
    private String appId;

    @Schema(required = true, value = "开放客户端名称", example = "DWixDuMagft5uoQXjKgC3m")
    @NotEmpty(message = "开放客户端名称不能为空")
    private String appName;

    @Schema(required = true, value = "开放客户端代码", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    @NotEmpty(message = "开放客户端代码不能为空")
    private String appNameEn;

    @Schema(required = true, value = "开放客户端类型(server-开放客户端服务 app-手机开放客户端 pc-PC网页开放客户端 wap-手机网页开放客户端)", allowableValues = "server,app,pc,wap", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    @NotEmpty(message = "开放客户端类型不能为空")
    private String appType;

    @Schema(required = true, value = "开放客户端分组", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    @NotEmpty(message = "开放客户端分组不能为空")
    private String groupCode;

    @Schema(required = true, value = "IP", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    @NotEmpty(message = "IP不能为空")
    private String ip;

    @Schema(required = false, value = "开放客户端图标", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    private String appIcon;

    @Schema(required = false, value = "手机开放客户端操作系统", allowableValues = "android,ios", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    private String appOs;

    @Schema(required = false, value = "开放客户端说明", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    private String appDesc;

    @Schema(required = true, allowableValues = "0,1", value = "是否启用")
    @NotNull(message = "是否启用不能为空")
    private Integer status;

    @Schema(required = false, value = "官网地址", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    private String website;

    @Schema(required = true, value = "开发者", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    @NotNull(message = "开发者不能为空")
    private Long developerId;

    @Schema(required = false, value = "是否开启验签", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    private Integer isSign = 0;

    @Schema(required = false, value = "是否开启加密", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    private Integer isEncrypt = 0;

    @Schema(required = false, value = "加密类型", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    private String encryptType;

    @Schema(required = false, value = "RSA公钥", example = "DWixDuMagft5uoQXjKgC3m", position = 4)
    private String publicKey;

}
