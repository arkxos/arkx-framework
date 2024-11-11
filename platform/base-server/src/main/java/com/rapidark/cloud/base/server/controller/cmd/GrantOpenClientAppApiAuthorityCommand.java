package com.rapidark.cloud.base.server.controller.cmd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/1 15:35
 */
@Data
@ApiModel(value = "应接口授权")
public class GrantOpenClientAppApiAuthorityCommand {

    @ApiModelProperty(name = "appId", value = "客户端Id", example = "", required = true)
    @NotNull(message = "客户端Id不能为空")
    private String appId;

    @ApiModelProperty(name = "appSystemCode", value = "应用系统代码", example = "", required = true)
    @NotEmpty(message = "应用系统代码不能为空")
    private String appSystemCode;

    @ApiModelProperty(name = "authorityIds", value = "过期时间", example = "", required = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @ApiModelProperty(name = "authorityIds", value = "接口ID:多个用,号隔开", example = "", required = false)
    private String authorityIds;

}
