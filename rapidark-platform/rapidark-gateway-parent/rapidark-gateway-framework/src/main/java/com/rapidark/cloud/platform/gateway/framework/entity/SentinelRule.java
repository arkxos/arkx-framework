package com.rapidark.cloud.platform.gateway.framework.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * @Description sentinel限流组件的规则配置表
 * @Author JL
 * @Date 2022/11/19
 * @Version V1.0
 */
@Entity
@Table(name="sentinelrule")
@Data
public class SentinelRule {

    /**
     * 主键，同routeId
     */
    @Id
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 2, max = 40, message = "告警ID长度必需在2到40个字符内")
    private String id;

    /**
     * 限流规则
     */
    @Column(name = "flowRule" )
    private String flowRule;

    /**
     * 熔断规则
     */
    @Column(name = "degradeRule" )
    private String degradeRule;


    /**
     * 系统保护规则
     */
    @Column(name = "systemRule" )
    private String systemRule;


    /**
     * 访问来源规则
     */
    @Column(name = "authoritRule" )
    private String authoritRule;

    /**
     * 热点参数规则
     */
    @Column(name = "paramFlowRule" )
    private String paramFlowRule;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updateTime" )
    private Date updateTime;

}
