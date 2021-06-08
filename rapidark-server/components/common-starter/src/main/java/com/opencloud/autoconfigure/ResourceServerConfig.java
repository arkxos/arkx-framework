package com.opencloud.autoconfigure;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @Author czx
 * @Description //TODO 资源服务器配置
 * @Date 17:03 2019/4/3
 **/
@Slf4j
@AllArgsConstructor
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final AuthIgnoreConfig authIgnoreConfig;

    /**
     * @Description //TODO http 请求的一些过滤配置
     **/
    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] urls = authIgnoreConfig.getIgnoreUrls().stream().distinct().toArray(String[]::new);
        http
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests().antMatchers(urls).permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }

}
