package com.rapidark.cloud.platform.gateway.manage.config;

import com.rapidark.framework.data.jpa.BaseRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
		basePackages = { "com.rapidark" },
		repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EntityScan({
		"com.rapidark.cloud.base.client.model.entity",
		"com.rapidark.cloud.platform.gateway.framework.entity"
})
public class JpaConfiguration {
}
