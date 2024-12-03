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
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
* @author Zheng Jie
* @date 2019-04-10
*/
@Entity
@Getter
@Setter
@Table(name="sys_dict_detail")
@Schema(description = "字典项")
public class SysDictItem extends AbstractIdLongEntity implements Serializable {

    @Id
    @Column(name = "detail_id")
    @NotNull(groups = Update.class)
    @Schema(title = "编号", hidden = true)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="oracleSeq")
    @SequenceGenerator(name="oracleSeq",sequenceName="SEQ_NEWSID",allocationSize=1)
    private Long id;

    @JoinColumn(name = "dict_id")
    @ManyToOne(fetch=FetchType.LAZY)
    @Schema(title = "所属字典", hidden = true)
    private SysDict sysDict;

	/**
	 * 类型
	 */
	@Schema(description = "类型")
	private String dictCode;

    @Schema(title = "字典标签")
    private String label;

    @Schema(title = "字典值")
    private String value;

    @Schema(title = "排序")
    private Integer sortOrder = 999;

	/**
	 * 描述
	 */
	@Schema(description = "描述")
	private String description;
}