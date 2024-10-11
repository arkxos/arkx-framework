package com.rapidark.cloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.common.model.BaseEntity;
import com.rapidark.common.mybatis.base.entity.AbstractEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author liuyadu
 */
@Getter
@Setter
@Entity
@Table(name="gateway_rate_limit_api")
public class GatewayRateLimitApi extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 限制数量
     */
    private Long policyId;

    /**
     * 时间间隔(秒)
     */
    private Long apiId;

}
