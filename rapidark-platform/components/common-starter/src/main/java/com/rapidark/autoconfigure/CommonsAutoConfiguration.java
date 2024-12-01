package com.rapidark.autoconfigure;

//import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.rapidark.framework.common.configuration.OpenCommonProperties;
import com.rapidark.framework.common.configuration.OpenIdGenProperties;
import com.rapidark.framework.common.exception.OpenGlobalExceptionHandler;
import com.rapidark.framework.common.exception.OpenRestResponseErrorHandler;
import com.rapidark.framework.common.filter.XssFilter;
import com.rapidark.framework.common.gen.SnowflakeIdGenerator;
import com.rapidark.framework.common.health.DbHealthIndicator;
import com.rapidark.framework.data.mybatis.handler.ModelMetaObjectHandler;
import com.rapidark.framework.common.security.http.OpenRestTemplate;
import com.rapidark.framework.common.security.oauth2.client.OpenOAuth2ClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * 默认配置类
 *
 * @author liuyadu
 */
@Slf4j
@org.springframework.boot.autoconfigure.AutoConfiguration
@EnableConfigurationProperties({OpenCommonProperties.class, OpenIdGenProperties.class, OpenOAuth2ClientProperties.class})
public class CommonsAutoConfiguration {
    /**
     * xss过滤
     * body缓存
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean XssFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new XssFilter());
        log.info("XssFilter [{}]", filterRegistrationBean);
        return filterRegistrationBean;
    }

    /**
     * 分页插件
     */
//    @ConditionalOnMissingBean(PaginationInterceptor.class)
//    @Bean
//    public PaginationInterceptor paginationInterceptor() {
//        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        log.info("PaginationInterceptor [{}]", paginationInterceptor);
//        return paginationInterceptor;
//    }

    /**
     * 默认加密配置
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(BCryptPasswordEncoder.class)
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        log.info("BCryptPasswordEncoder [{}]", encoder);
        return encoder;
    }

    /**
     * Spring上下文工具配置
     *
     * @return
     */
//    @Bean
//    @ConditionalOnMissingBean(ArkSpringContextHolder.class)
//    public ArkSpringContextHolder springContextHolder() {
//        ArkSpringContextHolder holder = new ArkSpringContextHolder();
//        log.info("SpringContextHolder [{}]", holder);
//        return holder;
//    }

    /**
     * 统一异常处理配置
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(OpenGlobalExceptionHandler.class)
    public OpenGlobalExceptionHandler exceptionHandler() {
        OpenGlobalExceptionHandler exceptionHandler = new OpenGlobalExceptionHandler();
        log.info("OpenGlobalExceptionHandler [{}]", exceptionHandler);
        return exceptionHandler;
    }

    /**
     * ID生成器配置
     *
     * @param properties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(OpenIdGenProperties.class)
    public SnowflakeIdGenerator snowflakeIdWorker(OpenIdGenProperties properties) {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(properties.getWorkId(), properties.getCenterId());
        log.info("SnowflakeIdGenerator [{}]", snowflakeIdGenerator);
        return snowflakeIdGenerator;
    }

    /**
     * 自定义注解扫描
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(RequestMappingScan.class)
    public RequestMappingScan resourceAnnotationScan(AmqpTemplate amqpTemplate) {
        RequestMappingScan scan = new RequestMappingScan(amqpTemplate);
        log.info("RequestMappingScan [{}]", scan);
        return scan;
    }

    /**
     * 自定义Oauth2请求类
     *
     * @param openCommonProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(OpenRestTemplate.class)
    public OpenRestTemplate openRestTemplate(OpenCommonProperties openCommonProperties, BusProperties busProperties, ApplicationEventPublisher publisher) {
        OpenRestTemplate restTemplate = new OpenRestTemplate(openCommonProperties, busProperties, publisher);
        //设置自定义ErrorHandler
        restTemplate.setErrorHandler(new OpenRestResponseErrorHandler());
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        log.info("OpenRestTemplate [{}]", restTemplate);
        return restTemplate;
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(5000);
        httpRequestFactory.setConnectTimeout(3000);
//        httpRequestFactory.setReadTimeout(3000);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        //设置自定义ErrorHandler
        restTemplate.setErrorHandler(new OpenRestResponseErrorHandler());
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        log.info("RestTemplate [{}]", restTemplate);
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(DbHealthIndicator.class)
    public DbHealthIndicator dbHealthIndicator() {
        DbHealthIndicator dbHealthIndicator = new DbHealthIndicator();
        log.info("DbHealthIndicator [{}]", dbHealthIndicator);
        return dbHealthIndicator;
    }

    /**
     * 自动填充模型数据
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ModelMetaObjectHandler.class)
    public ModelMetaObjectHandler modelMetaObjectHandler() {
        ModelMetaObjectHandler metaObjectHandler = new ModelMetaObjectHandler();
        log.info("ModelMetaObjectHandler [{}]", metaObjectHandler);
        return metaObjectHandler;
    }
}
