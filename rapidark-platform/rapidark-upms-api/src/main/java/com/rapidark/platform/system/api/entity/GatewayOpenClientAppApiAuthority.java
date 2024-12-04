package com.rapidark.platform.system.api.entity;


import com.rapidark.framework.data.jpa.entity.IdLongEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/1 16:03
 */
@Getter
@Setter
@Entity
@Table(name="Gateway_Open_Client_App_Api_Authority")
public class GatewayOpenClientAppApiAuthority extends IdLongEntity {

    @Column(name = "app_Id",nullable = false)
    @NotBlank(message = "客户端系统Id不能为空")
    @Schema(title = "客户端系统Id")
    private String appId;

    @Column(name = "app_system_code",nullable = false)
    @NotBlank(message = "应用系统编码不能为空")
    @Schema(title = "应用系统编码")
    private String appSystemCode;

    @Column(name = "authority_Id",nullable = false)
    @NotBlank(message = "权限ID不能为空")
    @Schema(title = "权限ID")
    private Long authorityId;

    /**
     * 过期时间:null表示长期
     */
    @Column(name = "expire_Time",nullable = true)
    @Schema(title = "过期时间")
    private LocalDateTime expireTime;

}
