package com.rapidark.cloud.base.client.model.entity;

import com.rapidark.framework.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 系统用户-登录日志
 *
 * @author liuyadu
 */
@Data
@Entity
@Table(name="base_account_logs")
public class BaseAccountLogs extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @ApiModelProperty(value = "ID")
    private Long id;

    private Date loginTime;

    /**
     * 登录Ip
     */
    private String loginIp;

    /**
     * 登录设备
     */
    private String loginAgent;

    /**
     * 登录次数
     */
    private Long loginNums;

    private Long userId;

    private String account;

    private String accountType;

    private String accountId;

    private String domain;

}
