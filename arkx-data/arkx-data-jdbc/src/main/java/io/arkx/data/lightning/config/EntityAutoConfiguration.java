package io.arkx.data.lightning.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

import java.util.Arrays;

@AutoConfiguration
public class EntityAutoConfiguration {

	// 配置中注册
	@Bean
	public JdbcCustomConversions jdbcCustomConversions() {
		return new JdbcCustomConversions(Arrays.asList(
				new EnumToIntegerConverter(),
				new IntegerToEnumConverter()
		));
	}

//	@Bean
//	public JdbcConverter jdbcConverter(DataSource dataSource, RelationalMappingContext context) {
//		JdbcCustomConversions conversions = new JdbcCustomConversions(
//				Arrays.asList(new EnumToIntegerConverter(),
//						new IntegerToEnumConverter())
//		);
//
//		return new MappingJdbcConverter(context, new CustomNamingStrategy(), conversions);
//	}

	// 自定义命名策略（如果需要）
//	@Bean
//	public JdbcMappingContext jdbcMappingContext(JdbcCustomConversions conversions) {
//		JdbcMappingContext context = new JdbcMappingContext();
//		context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
//		// 设置命名策略
//		context.setNamingStrategy(new CustomNamingStrategy());
//		return context;
//	}

}
