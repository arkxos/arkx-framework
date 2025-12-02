package io.arkx.framework.data.mybatis.pro.sample.springboot.domain;

import io.arkx.framework.data.mybatis.pro.core.annotations.Table;
import io.arkx.framework.data.mybatis.pro.service.entity.IdEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author w.dehai
 *
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("smart_dict")
public class Dict extends IdEntity {

	private Integer value;

	private String labelValue;

	private String enName;

	private String cnName;

	private Integer sort;

	private Integer status;

}
