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
 * 邮件模板配置
 *
 * @author admin
 * @date 2019-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("msg_email_template")
@Schema(description = "EmailTemplate对象", title = "邮件模板配置")
public class EmailTemplate extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "tpl_id", type = IdType.ASSIGN_ID)
    private Long tplId;

    @Schema(title = "模板名称")
    private String name;

    @Schema(title = "模板编码")
    private String code;

    @Schema(title = "发送服务器配置")
    private Long configId;

    @Schema(title = "模板")
    private String template;

    @Schema(title = "模板参数")
    private String params;
}
