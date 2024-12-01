package com.rapidark.cloud.msg.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rapidark.framework.data.mybatis.model.AbstractEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 邮件发送日志
 *
 * @author admin
 * @date 2019-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("msg_email_logs")
@Schema(description = "EmailLogs对象", title = "邮件发送日志")
public class EmailLogs extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    private Long logId;

    private String subject;

    private String sendTo;

    private String sendCc;

    private String content;

    @Schema(title = "附件路径")
    private String attachments;

    @Schema(title = "发送次数")
    private Integer sendNums;

    @Schema(title = "错误信息")
    private String error;

    @Schema(title = "0-失败 1-成功")
    private Integer result;

    @Schema(title = "发送配置")
    private String config;

    @Schema(title = "模板编号")
    private String tplCode;
}
