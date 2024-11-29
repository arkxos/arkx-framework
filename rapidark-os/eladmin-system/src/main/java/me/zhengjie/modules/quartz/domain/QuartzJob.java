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

import com.rapidark.framework.data.jpa.entity.AbstractIdLongEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Zheng Jie
 * @date 2019-01-07
 */
@Getter
@Setter
@Entity
@Table(name = "sys_quartz_job")
public class QuartzJob extends AbstractIdLongEntity implements Serializable {

    public static final String JOB_KEY = "JOB_KEY";

    @Id
    @Column(name = "job_id")
    @NotNull(groups = {Update.class})
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    @Schema(value = "用于子任务唯一标识", hidden = true)
    private String uuid;

    @Schema(value = "定时器名称")
    private String jobName;

    @NotBlank
    @Schema(value = "Bean名称")
    private String beanName;

    @NotBlank
    @Schema(value = "方法名称")
    private String methodName;

    @Schema(value = "参数")
    private String params;

    @NotBlank
    @Schema(value = "cron表达式")
    private String cronExpression;

    @Schema(value = "状态，暂时或启动")
    private Boolean isPause = false;

    @Schema(value = "负责人")
    private String personInCharge;

    @Schema(value = "报警邮箱")
    private String email;

    @Schema(value = "子任务")
    private String subTask;

    @Schema(value = "失败后暂停")
    private Boolean pauseAfterFailure;

    @NotBlank
    @Schema(value = "备注")
    private String description;
}