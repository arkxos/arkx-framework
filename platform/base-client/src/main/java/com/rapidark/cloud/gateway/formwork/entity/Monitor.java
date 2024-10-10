package com.rapidark.cloud.gateway.formwork.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Description 告警监控实体类
 * @Author JL
 * @Date 2021/04/14
 * @Version V1.0
 */
@Entity
@Table(name="GATEWAY_MONITOR")
@Data
public class Monitor {

    /**
     * 主键，同routeId
     */
    @Id
    @NotNull(message = "告警ID不能为空")
    @Size(min = 2, max = 40, message = "告警ID长度必需在2到40个字符内")
    private String id;

    /**
     * 告警状态：0启用，1禁用，2告警
     */
    @NotNull(message = "告警状态不能为空")
    @Column(name = "status" )
    private String status;

    /**
     * 通知接收邮箱 emails
     */
    @NotNull(message = "通知接收邮箱不能为空")
    @Column(name = "emails" )
    private String emails;

    /**
     * 告警邮件主题附带内容
     */
    @Column(name = "topic" )
    private String topic;

    /**
     * 告警频率：30m,1h,5h,12h,24h
     */
    @NotNull(message = "告警频率不能为空")
    @Column(name = "frequency" )
    @Size(min = 2, max = 4, message = "告警频率不能为空")
    private String frequency;

    /**
     * 告警重试，0开启，1禁用
     */
    @NotNull(message = "告警重试不能为空")
    @Column(name = "recover" )
    private String recover;

    /**
     * 告警时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "alarm_Time" )
    private Date alarmTime;

    /**
     * 发送告警时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "send_Time" )
    private Date sendTime;

    /**
     * 创建时间和修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "更新时间不能为空")
    @Column(name = "update_Time" )
    private Date updateTime;

}
