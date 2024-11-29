package com.rapidark.cloud.uaa.admin.server.controller.cmd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/25 14:53
 */
@Data
@Schema(description = "修改应用开发信息")
public class ThirdpartSystemLoginCommand {

    @Schema(required = true, value = "客户端id", example = "ECO6swuQ8eCtu9l6MMTdw0dA")
    @NotNull(message = "客户端id不能为空")
    private String clientId;
    @Schema(required = true, value = "客户端秘钥", example = "c0LZsvO1aqwU0MYAvnFODmrdNG0Au8zv")
    @NotNull(message = "客户端秘钥不能为空")
    private String clientSecret;

}
