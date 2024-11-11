package com.rapidark.cloud.gateway.formwork.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * 客户端注册网关路由服务实体类
 * @author darkness
 * @date 2022/5/30 14:34
 * @version 1.0
 */
@Entity
@Table(name="gateway_client_server_register")
@Data
public class ClientServerRegister {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "客户端ID不能为空")
    @Size(min = 2, max = 40, message = "客户端ID长度必需在2到40个字符内")
    @Column(name = "client_Id" )
    private String clientId;
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 2, max = 40, message = "网关路由ID长度必需在2到40个字符内")
    @Column(name = "route_Id")
    private String routeId;
    /**
     * token加密内容
     */
    @Column(name = "token")
    private String token;
    /**
     * token加密密钥
     */
    @Column(name = "secret_Key")
    private String secretKey;
    /**
     * token有效期截止时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "token_Effective_Time")
    private Date tokenEffectiveTime;
    /**
     * 状态，0是启用，1是禁用
     */
    @Column(name = "status")
    private String status;
    /**
     * 创建时间和修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "创建时间不能为空")
    @Column(name = "create_Time")
    private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_Time")
    private Date updateTime;
}
