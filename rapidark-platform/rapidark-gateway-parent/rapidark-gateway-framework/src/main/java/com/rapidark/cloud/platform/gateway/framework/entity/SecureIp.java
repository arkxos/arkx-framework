package com.rapidark.cloud.platform.gateway.framework.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * @Description IP实体类
 * @Author JL
 * @Date 2020/05/28
 * @Version V1.0
 */
@Entity
@Table(name="secureip")
@Data
public class SecureIp implements java.io.Serializable {

    @Id
    @NotNull(message = "IP名称不能为空")
    @Size(min = 4, max = 16, message = "IP名称字段长度必需在2到40个字符内")
    private String ip;

	@Column(name = "remarks")
	private String remarks;

    /**
     * 状态，0是启用，1是禁用
     */
    @NotNull(message = "IP状态不能为空")
    @Size(min = 1, max = 2, message = "状态字段长度必需在1个字符")
    @Column(name = "status")
    private String status;
    /**
     * 创建时间和修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "创建时间不能为空")
    @Column(name = "createTime")
    private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updateTime")
    private Date updateTime;


}
