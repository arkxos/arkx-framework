package com.flying.fish.formwork.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @Description 网关路由API接口文档实体类
 * @Author JL
 * @Date 2020/11/25
 * @Version V1.0
 */
@Entity
@Table(name="apidoc")
@Data
public class ApiDoc {

    /**
     * 主键，同route_id
     */
    @Id
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 1, max = 40, message = "网关路由ID长度必需在1到40个字符内")
    private String id;
    /**
     * 内容
     */
    @Column(name = "content" )
    private String content;

}
