package io.arkx.data.jdbc.repository.query.test;

/**
 * @author Nobody
 * @date 2025-07-17 1:15
 * @since 1.0
 */

import io.arkx.data.jdbc.repository.support.SqlToyJpaRepositoryFactoryBean;
import org.sagacity.sqltoy.configure.SqltoyAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Import(SqltoyAutoConfiguration.class)
@EnableJdbcRepositories(repositoryFactoryBeanClass = SqlToyJpaRepositoryFactoryBean.class)
@SpringBootApplication(scanBasePackages = { "io.arkx", "org.sagacity.sqltoy" })
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}