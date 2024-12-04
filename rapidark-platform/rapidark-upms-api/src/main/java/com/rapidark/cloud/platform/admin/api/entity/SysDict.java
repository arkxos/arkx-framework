/*
 *  Copyright 2019-2021 RapidArk
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.rapidark.cloud.platform.admin.api.entity;

import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@Entity
@Getter
@Setter
@Table(name="sys_dict")
@Schema(description = "字典类型")
public class SysDict extends AbstractIdLongEntity implements Serializable {

    @Id
    @Column(name = "dict_id")
    @NotNull(groups = Update.class)
    @Schema(title = "编号", hidden = true)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="oracleSeq")
    @SequenceGenerator(name="oracleSeq",sequenceName="SEQ_NEWSID",allocationSize=1)
    private Long id;

    @NotBlank
    @Schema(title = "编码")
    private String code;

    @Schema(title = "名称")
    private String name;

	/**
	 * 是否是系统内置
	 */
	@Schema(description = "是否系统内置")
	private String systemFlag;

	/**
	 * 备注信息
	 */
	@Schema(description = "备注信息")
	private String remarks;

	@OneToMany(mappedBy = "dict",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
	private List<SysDictItem> dictItems;

}