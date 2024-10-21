package com.rapidark.cloud.base.client.model.entity;

import com.rapidark.framework.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 系统资源-API接口
 * @author darkness
 * @date 2022/5/12 18:59
 * @version 1.0
 */
@Data
@Entity
@Table(name="base_api")
public class BaseApi extends BaseEntity {

    private static final long serialVersionUID = -9099562653030770650L;

    /**
     * 资源ID
     */
    @Id
    @Column(name = "api_id")
    @ApiModelProperty(value = "api_id")
    private Long apiId;

    /**
     * 资源编码
     */
    private String apiCode;

    /**
     * 资源名称
     */
    private String apiName;

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 接口分类
     */
    private String apiCategory;
    /**
     * 资源路径
     */
    private String path;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 资源描述
     */
    private String apiDesc;

    /**
     * 状态:0-无效 1-有效
     */
    private Integer status;

    /**
     * 保留数据0-否 1-是 不允许删除
     */
    private Integer isPersist;

    /**
     * 安全认证:0-否 1-是 默认:1
     */
    private Integer isAuth;

    /**
     * 是否公开访问: 0-内部的 1-公开的
     */
    private Integer isOpen;
    /**
     * 请求方式
     */
    private String requestMethod;
    /**
     * 响应类型
     */
    private String contentType;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

}
