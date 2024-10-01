package com.rapidark.cloud.gateway.formwork.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 网关路由实体类
 * @author darkness
 * @date 2022/5/24 14:21
 * @version 1.0
 */
@Entity
@Table(name="route")
@Data
public class Route implements java.io.Serializable {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 2, max = 40, message = "网关路由ID长度必需在2到40个字符内")
    private String id;
    @NotNull(message = "网关路由名称不能为空")
    @Size(min = 2, max = 40, message = "网关路由名称长度必需在2到40个字符内")
    @Column(name = "name" )
    private String name;
    @NotNull(message = "网关路由系统代号不能为空")
    @Size(min = 2, max = 40, message = "网关路由系统代号长度必需在2到40个字符内")
    @Column(name = "system_Code" )
    private String systemCode;
    @NotNull(message = "网关路由客户端分组不能为空")
    @Column(name = "group_Code")
    private String groupCode;
    @NotNull(message = "网关路由服务uri不能为空")
    @Size(min = 2, max = 40, message = "网关路由服务uri长度必需在2到200个字符内")
    @Column(name = "uri")
    private String uri;
    @NotNull(message = "网关路由断言Path不能为空")
    @Size(min = 2, max = 40, message = "网关路由断言Path长度必需在2到100个字符内")
    @Column(name = "path")
    private String path;
    /**
     * 请求类型：POST，GET，DELETE，PUT
     */
    @Column(name = "method")
    private String method;
    /**
     * 断言Hosts
     */
    @Column(name = "host")
    private String host;
    /**
     * 断言RemoteAddrs
     */
    @Column(name = "remote_Addr")
    private String remoteAddr;
    /**
     * 断言Headers
     */
    @Column(name = "header")
    private String header;
    /**
     * 鉴权过滤器类型：ip,token,id
     */
    @Column(name = "filter_Gatewa_Name")
    private String filterGatewaName;
    /**
     * 熔断器名称：hystrix,custom
     */
    @Column(name = "filter_Hystrix_Name")
    private String filterHystrixName;
    /**
     * 限流器类型：ip,uri,requestId
     */
    @Column(name = "filter_Rate_Limiter_Name")
    private String filterRateLimiterName;
    /**
     * 过滤器类型：header,ip,param,time,cookie
     */
    @Column(name = "filter_Authorize_Name")
    private String filterAuthorizeName;
    /**
     * 回滚消息
     */
    @Column(name = "fallback_Msg")
    private String fallbackMsg;
    /**
     * 回滚超时时长
     */
    @Column(name = "fallback_Timeout")
    private Long fallbackTimeout;
    /**
     * 每1秒限制请求数(令牌数)
     */
    @Column(name = "replenish_Rate")
    private Integer replenishRate;
    /**
     * 令牌桶的容量
     */
    @Column(name = "burst_Capacity")
    private Integer burstCapacity;
    @Transient
    private String weightName;
    @Transient
    private Integer weight;
    /**
     * 状态，0是启用，1是禁用
     */
    @Column(name = "status")
    private String status;
    /**
     * 断言前缀截取层数
     */
    @Column(name = "strip_Prefix")
    private Integer stripPrefix;
    /**
     * 请求参数
     */
    @Column(name = "request_Parameter")
    private String requestParameter;
    /**
     * 重写Path路径
     */
    @Column(name = "rewrite_Path")
    private String rewritePath ;
    /**
     * 鉴权：Header
     */
    @Column(name = "access_Header")
    private String accessHeader;
    /**
     * 鉴权：Header
     */
    @Column(name = "access_Ip")
    private String accessIp;
    /**
     * 鉴权：Parameter
     */
    @Column(name = "access_Parameter")
    private String accessParameter;
    /**
     * 鉴权：Time
     */
    @Column(name = "access_Time")
    private String accessTime;
    /**
     * 鉴权：Cookie
     */
    @Column(name = "access_Cookie")
    private String accessCookie;
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
