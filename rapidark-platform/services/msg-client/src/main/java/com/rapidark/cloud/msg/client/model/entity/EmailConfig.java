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
 * 邮件发送配置
 *
 * @author admin
 * @date 2019-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("msg_email_config")
@Schema(description = "EmailConfig对象", title = "邮件发送配置")
public class EmailConfig extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "config_id", type = IdType.ASSIGN_ID)
    private Long configId;

    @Schema(title = "配置名称")
    private String name;

    @Schema(title = "发件服务器域名")
    private String smtpHost;

    @Schema(title = "发件服务器账户")
    private String smtpUsername;

    @Schema(title = "发件服务器密码")
    private String smtpPassword;

    @Schema(title = "保留数据0-否 1-是 不允许删除")
    private Integer isPersist;

    @Schema(title = "是否为默认 0-否 1-是 ")
    private Integer isDefault;
}
