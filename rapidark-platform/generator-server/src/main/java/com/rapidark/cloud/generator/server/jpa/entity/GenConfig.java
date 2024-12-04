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
package com.rapidark.cloud.generator.server.jpa.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 代码生成配置
 * @author Zheng Jie
 * @date 2019-01-03
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "code_gen_config")
public class GenConfig implements Serializable {

    public GenConfig(String tableName) {
        this.tableName = tableName;
    }

    @Id
    @Column(name = "config_id")
    @Schema(title = "ID", hidden = true)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="oracleSeq")
//    @SequenceGenerator(name="oracleSeq",sequenceName="SEQ_NEWSID",allocationSize=1)
    private Long id;

    @NotBlank
    @Schema(title = "表名")
    private String tableName;

    @NotBlank
    @Schema(title = "表英文名")
    private String tableEnName;

    @Schema(title = "接口名称")
    private String apiAlias;

    @NotBlank
    @Schema(title = "包路径")
    private String pack;

    @NotBlank
    @Schema(title = "模块名")
    private String moduleName;

    @NotBlank
    @Schema(title = "前端文件路径")
    private String path;

    @Schema(title = "前端文件路径")
    private String apiPath;

    @Schema(title = "作者")
    private String author;

    @Schema(title = "表前缀")
    private String prefix;

    @Schema(title = "是否覆盖")
    private Boolean cover = false;

}
