package com.rapidark.cloud.platform.gateway.framework.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Description 负载服务实体类
 * @Author JL
 * @Date 2020/06/28
 * @Version V1.0
 */
@Entity
@Table(name="loadserver")
@Data
public class LoadServer implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "路由主键ID值不能为空")
    @Column(name = "routeId")
    private String routeId;
    @NotNull(message = "负载ID值不能为空")
    @Column(name = "balancedId")
    private String balancedId;
    @NotNull(message = "网关路由权重值不能为空")
    @Column(name = "weight")
    private Integer weight;
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
