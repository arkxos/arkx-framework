package com.rapidark.cloud.gateway.formwork.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Description IP实体类
 * @Author jianglong
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
    @Column(name = "remarks")
    private String remarks;

}
