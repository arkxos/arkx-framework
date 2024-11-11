package com.rapidark.cloud.gateway.manage.service.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rapidark.framework.data.jpa.entity.Status;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/27 17:25
 */
@Data
@ApiModel(value = "修改开发商参数")
public class UpdateDeveloperCommand {

    @ApiModelProperty(name = "id", value = "Id", example = "", required = true)
    @NotNull(message = "Id不能为空")
    private Long id;

    /**
     * 开发者类型:isp-服务提供商 dev-自研开发者
     */
    @ApiModelProperty(name = "type", value = "公司类型", example = "", required = true)
    @NotNull(message = "公司类型不能为空")
    private Integer type;

    @ApiModelProperty(name = "companyName", value = "公司名称", example = "", required = true)
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @ApiModelProperty(name = "personName", value = "联系人", example = "", required = true)
    @NotNull(message = "联系人不能为空")
    private String personName;

    @ApiModelProperty(name = "mobile", value = "手机号", example = "", required = true)
    @NotNull(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty(name = "companyId", value = "企业ID", example = "", required = true)
    private Long companyId;

    @ApiModelProperty(name = "nickName", value = "昵称", example = "", required = true)
    @NotNull(message = "昵称不能为空")
    private String nickName;

    @ApiModelProperty(name = "avatar", value = "头像", example = "", required = true)
    private String avatar;

    @ApiModelProperty(name = "email", value = "邮箱", example = "", required = true)
    private String email;

    @ApiModelProperty(name = "userDesc", value = "描述", example = "", required = true)
    private String userDesc;

    @ApiModelProperty(name = "status", value = "状态:0-禁用 1-正常 2-锁定", example = "", required = true)
    @NotNull(message = "状态不能为空")
    private Status status;

}
