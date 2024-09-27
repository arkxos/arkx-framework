package com.rapidark.cloud.gateway.server.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.rapidark.cloud.gateway.server.filter.RemoveGatewayContextFilter;
import com.rapidark.cloud.gateway.server.locator.ResourceLocator;
import com.rapidark.common.configuration.OpenCommonProperties;
import com.rapidark.common.utils.SpringContextHolder;
import com.rapidark.cloud.gateway.server.actuator.ApiEndpoint;
import com.rapidark.cloud.gateway.server.exception.JsonExceptionHandler;
import com.rapidark.cloud.gateway.server.exception.RequestDecryptionExceptionHandler;
import com.rapidark.cloud.gateway.server.filter.GatewayContextFilter;
import com.rapidark.cloud.gateway.server.filter.RouteToUrlFilter;
import com.rapidark.cloud.gateway.server.locator.JdbcRouteDefinitionLocator;
import com.rapidark.cloud.gateway.server.service.AccessLogService;
import com.rapidark.cloud.gateway.server.service.feign.BaseAppServiceClient;
import com.rapidark.cloud.gateway.server.service.feign.BaseAuthorityServiceClient;
import com.rapidark.cloud.gateway.server.service.feign.GatewayServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * 网关配置类
 * @author darkness
 * @date 2021/4/29 16:22
 * @version 1.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({ApiProperties.class, OpenCommonProperties.class})
public class GatewayConfiguration {
    @Bean
    @ConditionalOnMissingBean(SpringContextHolder.class)
    public SpringContextHolder springContextHolder() {
        SpringContextHolder holder = new SpringContextHolder();
        log.info("SpringContextHolder [{}]", holder);
        return holder;
    }

    /**
     * 自定义异常处理[@@]注册Bean时依赖的Bean，会从容器中直接获取，所以直接注入即可
     *
     * @param viewResolversProvider
     * @param accessLogService
     * @return
     */
    @Primary
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ObjectProvider<List<ViewResolver>> viewResolversProvider, AccessLogService accessLogService) {
        ServerCodecConfigurer serverCodecConfigurer = ServerCodecConfigurer.create();
        JsonExceptionHandler jsonExceptionHandler = new JsonExceptionHandler(accessLogService);
        jsonExceptionHandler.setViewResolvers(viewResolversProvider.getIfAvailable(Collections::emptyList));
        jsonExceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        jsonExceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        log.info("ErrorWebExceptionHandler [{}]", jsonExceptionHandler);
        return jsonExceptionHandler;
    }

    /**
     * Jackson全局配置
     *
     * @param properties
     * @return
     */
    @Bean
    @Primary
    public JacksonProperties jacksonProperties(JacksonProperties properties) {
        properties.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        properties.getSerialization().put(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        properties.setDateFormat("yyyy-MM-dd HH:mm:ss");
        properties.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        log.info("JacksonProperties [{}]", properties);
        return properties;
    }

    /**
     * 转换器全局配置
     *
     * @return
     */
    @Bean
    public HttpMessageConverters httpMessageConverters() {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        // 不忽略为空的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
//        objectMapper.getSerializationConfig().withFeatures(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        /**
         * 序列换成json时,将所有的long变成string
         * js中long过长精度丢失
         */
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        // 自定义 全局把时间转为 时间戳
//        javaTimeModule.addSerializer(Date.class, new DateToLongSerializer());
//        javaTimeModule.addSerializer(LocalDate.class, new LocalDateToLongSerializer());
//        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeToLongSerializer());

        objectMapper.registerModule(javaTimeModule);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        log.info("MappingJackson2HttpMessageConverter [{}]", jackson2HttpMessageConverter);
        return new HttpMessageConverters(jackson2HttpMessageConverter);
    }

    @Bean
    @Primary
    public SwaggerProvider swaggerProvider(RouteDefinitionLocator routeDefinitionLocator) {
        return new SwaggerProvider(routeDefinitionLocator);
    }

    /**
     * 动态路由加载
     *
     * @return
     */
    @Bean
    public JdbcRouteDefinitionLocator jdbcRouteDefinitionLocator(JdbcTemplate jdbcTemplate, InMemoryRouteDefinitionRepository repository) {
        JdbcRouteDefinitionLocator jdbcRouteDefinitionLocator = new JdbcRouteDefinitionLocator(jdbcTemplate, repository);
        log.info("JdbcRouteDefinitionLocator [{}]", jdbcRouteDefinitionLocator);
        return jdbcRouteDefinitionLocator;
    }

    /**
     * 动态路由加载
     *
     * @return
     */
    @Bean
    @Lazy
    public ResourceLocator resourceLocator(RouteDefinitionLocator routeDefinitionLocator, BaseAuthorityServiceClient baseAuthorityServiceClient, GatewayServiceClient gatewayServiceClient) {
        ResourceLocator resourceLocator = new ResourceLocator(routeDefinitionLocator, baseAuthorityServiceClient, gatewayServiceClient);
        log.info("ResourceLocator [{}]", resourceLocator);
        return resourceLocator;
    }

    /**
     * 网关bus端点
     *
     * @param context
     * @param bus
     * @return
     */
    @Bean
    @ConditionalOnAvailableEndpoint
    @ConditionalOnClass({Endpoint.class})
    public ApiEndpoint apiEndpoint(ApplicationContext context, BusProperties bus) {
        ApiEndpoint endpoint = new ApiEndpoint(context, bus.getId());
        log.info("ApiEndpoint [{}]", endpoint);
        return endpoint;
    }

    @Bean
    @ConditionalOnMissingBean(GatewayContextFilter.class)
    public GatewayContextFilter gatewayContextFilter(BaseAppServiceClient baseAppServiceClient, ApiProperties apiProperties, AccessLogService accessLogService) {
        log.debug("Load GatewayContextFilter Config Bean");
        return new GatewayContextFilter(baseAppServiceClient, apiProperties, new RequestDecryptionExceptionHandler(accessLogService));
    }

    @Bean
    @ConditionalOnMissingBean(RemoveGatewayContextFilter.class)
    public RemoveGatewayContextFilter removeGatewayContextFilter() {
        RemoveGatewayContextFilter gatewayContextFilter = new RemoveGatewayContextFilter();
        log.debug("Load RemoveGatewayContextFilter Config Bean");
        return gatewayContextFilter;
    }

    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }

    @Bean
    public RouteToUrlFilter routeToUrlFilter() {
        return new RouteToUrlFilter();
    }
}
