package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 开放网关-访问日志
 *
 * @author liuyadu
 */
@Getter
@Setter
@Entity
@Table(name="gateway_access_logs")
public class GatewayAccessLogs extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 访问ID
     */
    @Id
    @Column(name = "access_Id")
    @ApiModelProperty(value = "accessId")
    private Long accessId;

    /**
     * 访问路径
     */
    private String path;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 响应状态
     */
    private String httpStatus;

    private String bizId;

    private Integer bizStatus;

    private String responseBody;

    /**
     * 请求时间
     */
    private Date requestTime;

    /**
     * 响应时间
     */
    private Date responseTime;

    /**
     * 耗时
     */
    private Long useTime;

    /**
     * 请求数据
     */
    private String params;

    /**
     * 请求头
     */
    private String headers;

    private String userAgent;

    /**
     * 区域
     */
    private String region;

    /**
     * 认证用户信息
     */
    private String authentication;

    /**
     * 服务名
     */
    private String serviceId;

    /**
     * 错误信息
     */
    private String error;

}
