package io.arkx.soa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.arkx.framework.data.jpa.BaseRepositoryFactoryBean;

/**
 * @author Darkness
 * @date 2020年10月25日 下午3:58:33
 * @version V1.0
 */
@Configuration
@EnableJpaRepositories(basePackages = {"io.arkx", "io.arkx"},
        repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EntityScan({"io.arkx.soa.entity"})
public class SoaConfig {

}
