package com.rapidark.cloud.msg.server.service;

import com.rapidark.framework.common.mybatis.base.service.IBaseService;
import com.rapidark.cloud.msg.client.model.entity.EmailTemplate;

/**
 * 邮件模板配置 服务类
 *
 * @author admin
 * @date 2019-07-25
 */
public interface EmailTemplateService extends IBaseService<EmailTemplate> {
    EmailTemplate getByCode(String code);
}
