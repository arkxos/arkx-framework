package com.rapidark.cloud.platform.gateway.framework.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * 网关应用服务路由
 * @author darkness
 * @date 2022/5/24 14:21
 * @version 1.0
 */
@Entity
// gateway_app_route
@Table(name="gw_route")
@Data
public class GatewayAppRoute implements java.io.Serializable {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 2, max = 40, message = "网关路由ID长度必需在2到40个字符内")
    private String id;
	/**
	 * 服务ID
	 * 考虑路由跟服务是否需要拆分开
	 */
//	private String serviceId;
	@NotNull(message = "网关路由系统代号不能为空")
	@Size(min = 2, max = 40, message = "网关路由系统代号长度必需在2到40个字符内")
	@Column(name = "systemCode" )
	private String systemCode;

    @NotNull(message = "网关路由名称不能为空")
    @Size(min = 2, max = 40, message = "网关路由名称长度必需在2到40个字符内")
    @Column(name = "name" )
    private String name;

	/**
	 * 路由类型:service-负载均衡 url-反向代理
	 */
	@NotBlank(message = "路由类型不能为空")
	@Size(min = 1, max = 40, message = "路由类型长度必需在2到40个字符内")
	@Column(name = "type")
	private String type;

    @NotNull(message = "网关路由客户端分组不能为空")
    @Column(name = "groupCode")
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
    @Column(name = "remoteAddr")
    private String remoteAddr;
    /**
     * 断言Headers
     */
    @Column(name = "header")
    private String header;
    /**
     * 鉴权过滤器类型：ip,token,id
     */
    @Column(name = "filterGatewayName")
    private String filterGatewayName;
    /**
     * 熔断器名称：hystrix,custom
     */
    @Column(name = "filterHystrixName")
    private String filterHystrixName;
    /**
     * 限流器类型：ip,uri,requestId
     */
    @Column(name = "filterRateLimiterName")
    private String filterRateLimiterName;
    /**
     * 过滤器类型：header,ip,param,time,cookie
     */
    @Column(name = "filterAuthorizeName")
    private String filterAuthorizeName;
    /**
     * 回滚消息
     */
    @Column(name = "fallbackMsg")
    private String fallbackMsg;
    /**
     * 回滚超时时长
     */
    @Column(name = "fallbackTimeout")
    private Long fallbackTimeout;
    /**
     * 限流策略名称
     */
    @Column(name = "flowRuleName")
    private String flowRuleName;
    /**
     * 熔断策略名称
     */
    @Column(name = "degradeRuleName")
    private String degradeRuleName;
    /**
     * 每1秒限制请求数(令牌数)
     */
    @Column(name = "replenishRate")
    private Integer replenishRate;
    /**
     * 令牌桶的容量
     */
    @Column(name = "burstCapacity")
    private Integer burstCapacity;
    @Transient
    private String weightName;
    @Transient
    private Integer weight;

    /**
     * 断言前缀截取层数
     */
    @Column(name = "stripPrefix")
    private Integer stripPrefix;
    /**
     * 请求参数
     */
    @Column(name = "requestParameter")
    private String requestParameter;
    /**
     * 重写Path路径
     */
    @Column(name = "rewritePath")
    private String rewritePath ;
    /**
     * 鉴权：Header
     */
    @Column(name = "accessHeader")
    private String accessHeader;
    /**
     * 鉴权：Header
     */
    @Column(name = "accessIp")
    private String accessIp;
    /**
     * 鉴权：Parameter
     */
    @Column(name = "accessParameter")
    private String accessParameter;
    /**
     * 鉴权：Time
     */
    @Column(name = "accessTime")
    private String accessTime;
    /**
     * 鉴权：Cookie
     */
    @Column(name = "accessCookie")
    private String accessCookie;
    /**
     * 缓存时长
     */
    @Column(name = "cacheTtl")
    private Long cacheTtl;

	/**
	 * 状态，0是启用，1是禁用
	 * 状态，1是启用，0是禁用
	 * @TODO 需调整
	 */
	@Column(name = "status")
	private String status;

	/**
	 * 0-不重试 1-重试
	 */
	private Integer retryable;

	/**
	 * 保留数据0-否 1-是 不允许删除
	 */
	@Column(name = "is_Persist")
	private Integer isPersist;

	/**
	 * 路由说明
	 */
	private String remark;

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
