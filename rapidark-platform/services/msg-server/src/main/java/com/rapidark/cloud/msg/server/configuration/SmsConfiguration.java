package com.rapidark.cloud.msg.server.configuration;

import com.rapidark.cloud.msg.server.service.SmsSender;
import com.rapidark.cloud.msg.server.service.impl.AliyunSmsSenderImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author woodev
 */
@Configuration
@EnableConfigurationProperties({AliyunSmsProperties.class})
@Slf4j
public class SmsConfiguration {
    @Bean
    @ConditionalOnClass({AliyunSmsSenderImpl.class})
    public SmsSender smsSender(AliyunSmsProperties aliyunSmsProperties) {
        AliyunSmsSenderImpl sender = new AliyunSmsSenderImpl();
        BeanUtils.copyProperties(aliyunSmsProperties, sender);
        return sender;
    }
}
