package com.rapidark.cloud.uaa.admin.server.controller.cmd;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录对象
 *
 * @author Lion Li
 */
@Data
@NoArgsConstructor
public class LoginBody {

    /**
     * 客户端id
     */
    @NotBlank(message = "{auth.clientid.not.blank}")
    private String clientId;

    /**
     * 授权类型
     */
    @NotBlank(message = "{auth.grant.type.not.blank}")
    private String grantType;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String verKey;

    /**
     * 用户名
     */
//    @NotBlank(message = "{user.username.not.blank}")
//    @Length(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = "{user.username.length.valid}")
    private String username;

    /**
     * 用户密码
     */
//    @NotBlank(message = "{user.password.not.blank}")
//    @Length(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = "{user.password.length.valid}")
    private String password;

}

