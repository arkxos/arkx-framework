package com.arkxos.soa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.arkxos.framework.data.jpa.BaseRepositoryFactoryBean;

/**
 * @author Darkness
 * @date 2020年10月25日 下午3:58:33
 * @version V1.0
 */
@Configuration
@EnableJpaRepositories(
		basePackages = { "com.xdreamaker","com.rapidark" }, 
		repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EntityScan({
	"com.rapidark.soa.entity"
})
public class SoaConfig {

}
