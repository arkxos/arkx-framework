package com.rapidark.cloud.base.client.model;

import com.rapidark.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/1 16:03
 */
@Getter
@Setter
@Entity
@Table(name="Gateway_Open_Client_App_Api_Authority")
public class GatewayOpenClientAppApiAuthority extends BaseEntity {

    @Id
    @Column(name = "ID")
    @ApiModelProperty(value = "ID")
    private String id;

    @Column(name = "app_Id",nullable = false)
    @NotBlank(message = "客户端系统Id不能为空")
    @ApiModelProperty(value = "客户端系统Id")
    private String appId;

    @Column(name = "app_system_code",nullable = false)
    @NotBlank(message = "应用系统编码不能为空")
    @ApiModelProperty(value = "应用系统编码")
    private String appSystemCode;

    @Column(name = "authority_Id",nullable = false)
    @NotBlank(message = "权限ID不能为空")
    @ApiModelProperty(value = "权限ID")
    private String authorityId;

    /**
     * 过期时间:null表示长期
     */
    @Column(name = "expire_Time",nullable = true)
    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expireTime;

}
