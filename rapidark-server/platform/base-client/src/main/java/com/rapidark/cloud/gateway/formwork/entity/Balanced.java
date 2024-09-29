package com.rapidark.cloud.gateway.formwork.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Description 负载管理实体类
 * @Author jianglong
 * @Date 2020/06/28
 * @Version V1.0
 */
@Entity
@Table(name="balanced")
@Data
public class Balanced implements java.io.Serializable {

    @Id
    @NotNull(message = "负载ID不能为空")
    @Size(min = 2, max = 40, message = "负载ID长度必需在2到40个字符内")
    private String id;
    @NotNull(message = "负载名称不能为空")
    @Size(min = 2, max = 40, message = "负载名称长度必需在2到40个字符内")
    @Column(name = "name")
    private String name;
    @NotNull(message = "客户端分组不能为空")
    @Column(name = "groupCode")
    private String groupCode;
    @NotNull(message = "网关负载服务uri不能为空")
    @Size(min = 2, max = 200, message = "网关负载服务uri长度必需在2到200个字符内")
    @Column(name = "loadUri")
    private String loadUri;
    /**
     * 状态，0是启用，1是禁用
     */
    @Column(name = "status")
    private String status;
    /**
     * 创建时间和修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @NotNull(message = "创建时间不能为空")
    @Column(name = "createTime")
    private Date createTime;
    @Column(name = "updateTime")
    private Date updateTime;
    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

}
