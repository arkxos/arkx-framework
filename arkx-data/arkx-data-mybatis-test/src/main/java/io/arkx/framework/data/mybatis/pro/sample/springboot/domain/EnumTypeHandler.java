package io.arkx.framework.data.mybatis.pro.sample.springboot.domain;

import io.arkx.framework.data.mybatis.pro.core.annotations.Id;
import io.arkx.framework.data.mybatis.pro.core.annotations.Table;

import lombok.Data;

@Data
@Table("smart_typehandler")
public class EnumTypeHandler {

	@Id
	private Long id;

	private Gender gender;

	private Integer status;

}
