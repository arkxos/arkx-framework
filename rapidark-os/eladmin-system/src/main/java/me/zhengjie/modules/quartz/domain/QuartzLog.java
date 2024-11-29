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
package me.zhengjie.modules.quartz.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Zheng Jie
 * @date 2019-01-07
 */
@Entity
@Data
@Table(name = "sys_quartz_log")
public class QuartzLog implements Serializable {

    @Id
    @Column(name = "log_id")
    @Schema(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(value = "任务名称", hidden = true)
    private String jobName;

    @Schema(value = "bean名称", hidden = true)
    private String beanName;

    @Schema(value = "方法名称", hidden = true)
    private String methodName;

    @Schema(value = "参数", hidden = true)
    private String params;

    @Schema(value = "cron表达式", hidden = true)
    private String cronExpression;

    @Schema(value = "状态", hidden = true)
    private Boolean isSuccess;

    @Schema(value = "异常详情", hidden = true)
    private String exceptionDetail;

    @Schema(value = "执行耗时", hidden = true)
    private Long time;

    @CreationTimestamp
    @Schema(value = "创建时间", hidden = true)
    private Timestamp createTime;
}
