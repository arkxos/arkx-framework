package io.arkx.framework.data.mybatis.pro.sample.springboot.domain;

import io.arkx.framework.data.mybatis.pro.core.annotations.Column;
import io.arkx.framework.data.mybatis.pro.core.annotations.Id;
import io.arkx.framework.data.mybatis.pro.core.annotations.Table;
import io.arkx.framework.data.mybatis.pro.core.annotations.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table("smart_user")
public class User {

	@Id
	private Long id;

	private String name;

	private String password;

	private Long version;

	@Transient
	private Integer gender;

	private String phoneNo;

	@Column("addr_info")
	private String addr;

	private String orderId;

	private Integer status;

}
