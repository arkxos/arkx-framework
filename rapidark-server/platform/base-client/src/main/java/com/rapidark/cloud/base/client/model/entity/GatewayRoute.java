//package com.rapidark.cloud.base.client.model.entity;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.rapidark.common.mybatis.base.entity.AbstractEntity;
//
///**
// * 网关动态路由
// * @author darkness
// * @date 2022/5/12 18:48
// * @version 1.0
// */
//@TableName("gateway_app_route")
//public class GatewayRoute extends AbstractEntity {
//
//    private static final long serialVersionUID = -2952097064941740301L;
//
//    /**
//     * 路由ID
//     */
//    @TableId(type = IdType.ASSIGN_ID)
//    private Long id;
//
//    /**
//     * 路由名称
//     */
//    private String systemCode;
//
//    /**
//     * 路由类型:service-负载均衡 url-反向代理
//     */
//    private String type;
//
//    /**
//     * 路径
//     */
//    private String path;
//
//    /**
//     * 服务ID
//     */
//    private String serviceId;
//
//    /**
//     * 完整地址
//     */
//    private String uri;
//
//    /**
//     * 忽略前缀
//     */
//    private Integer stripPrefix;
//
//    /**
//     * 0-不重试 1-重试
//     */
//    private Integer retryable;
//
//    /**
//     * 状态:0-无效 1-有效
//     */
//    private Integer status;
//
//    /**
//     * 保留数据0-否 1-是 不允许删除
//     */
//    private Integer isPersist;
//
//    /**
//     * 路由说明
//     */
//    private String name;
//
//    /**
//     * 获取路径
//     *
//     * @return path - 路径
//     */
//    public String getPath() {
//        return path;
//    }
//
//    /**
//     * 设置路径
//     *
//     * @param path 路径
//     */
//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    /**
//     * 获取服务ID
//     *
//     * @return service_id - 服务ID
//     */
//    public String getServiceId() {
//        return serviceId;
//    }
//
//    /**
//     * 设置服务ID
//     *
//     * @param serviceId 服务ID
//     */
//    public void setServiceId(String serviceId) {
//        this.serviceId = serviceId;
//    }
//
//    /**
//     * 获取完整地址
//     *
//     * @return url - 完整地址
//     */
//    public String getUri() {
//        return uri;
//    }
//
//    /**
//     * 设置完整地址
//     *
//     * @param uri 完整地址
//     */
//    public void setUri(String uri) {
//        this.uri = uri;
//    }
//
//    /**
//     * 获取忽略前缀
//     *
//     * @return strip_prefix - 忽略前缀
//     */
//    public Integer getStripPrefix() {
//        return stripPrefix;
//    }
//
//    /**
//     * 设置忽略前缀
//     *
//     * @param stripPrefix 忽略前缀
//     */
//    public void setStripPrefix(Integer stripPrefix) {
//        this.stripPrefix = stripPrefix;
//    }
//
//    /**
//     * 获取0-不重试 1-重试
//     *
//     * @return retryable - 0-不重试 1-重试
//     */
//    public Integer getRetryable() {
//        return retryable;
//    }
//
//    /**
//     * 设置0-不重试 1-重试
//     *
//     * @param retryable 0-不重试 1-重试
//     */
//    public void setRetryable(Integer retryable) {
//        this.retryable = retryable;
//    }
//
//    public String getSystemCode() {
//        return systemCode;
//    }
//
//    public void setSystemCode(String systemCode) {
//        this.systemCode = systemCode;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public Integer getStatus() {
//        return status;
//    }
//
//    public void setStatus(Integer status) {
//        this.status = status;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Integer getIsPersist() {
//        return isPersist;
//    }
//
//    public void setIsPersist(Integer isPersist) {
//        this.isPersist = isPersist;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//}
