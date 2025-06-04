package io.arkx.framework.data.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;

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
