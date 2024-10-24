package com.rapidark.framework.data.jpa.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * @author Darkness
 * @date 2019-08-18 13:56:12
 * @version V1.0
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseSortEntity<PK extends Serializable> extends BaseEntity<PK> {

	@Column(name = "SORT_ORDER")
	private long sortOrder = 0;// 排序号

}
