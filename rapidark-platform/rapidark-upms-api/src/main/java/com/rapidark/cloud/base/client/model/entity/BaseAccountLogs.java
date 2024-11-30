package com.rapidark.cloud.base.client.model.entity;


import com.rapidark.framework.data.jpa.entity.IdLongEntity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * 系统用户-登录日志
 *
 * @author liuyadu
 */
@Data
@Entity
@Table(name="base_account_logs")
public class BaseAccountLogs extends IdLongEntity {

    private static final long serialVersionUID = 1L;

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
