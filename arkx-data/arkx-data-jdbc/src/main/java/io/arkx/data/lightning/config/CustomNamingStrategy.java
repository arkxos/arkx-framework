package io.arkx.data.lightning.config;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.DefaultNamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;

/**
 * @author Nobody
 * @version 1.0
 * @date 2025-09-06 22:01
 * @since 1.0
 */
public class CustomNamingStrategy extends DefaultNamingStrategy {

	@Override
	public String getColumnName(RelationalPersistentProperty property) {
		// 优先使用方法上的注解
		if (property.isAnnotationPresent(Column.class)) {
			return property.getRequiredAnnotation(Column.class).value();
		}
		// 自定义逻辑：ID属性特殊处理
		// if ("id".equals(property.getName())) {
		// return property.getOwner().getTableName().getReference().toLowerCase() +
		// "_id";
		// }
		return super.getColumnName(property);
	}

}
