package io.arkx.data.lightning.jdbc;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-09-10 21:05
 * @since 1.0
 */
import io.arkx.framework.data.common.entity.Status;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用反射映射器 - 自动将结果集映射到指定类型的对象
 * @param <T> 目标对象类型
 */
public class GenericReflectionRowMapper<T> implements RowMapper<T> {

	private final Class<T> targetClass;
	private final Map<String, Field> fieldMap = new HashMap<>();

	public GenericReflectionRowMapper(Class<T> targetClass) {
		this.targetClass = targetClass;
		// 预先缓存类的字段信息（包括父类）
		cacheClassFields(targetClass);
	}

	private void cacheClassFields(Class<?> clazz) {
		if (clazz == null || clazz.equals(Object.class)) {
			return;
		}

		// 递归处理父类
		cacheClassFields(clazz.getSuperclass());

		// 缓存当前类的字段
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true); // 允许访问私有字段
			fieldMap.put(field.getName().toLowerCase(), field);
		}
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			T obj = targetClass.getDeclaredConstructor().newInstance();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			for (int i = 1; i <= columnCount; i++) {
				String columnName = JdbcUtils.lookupColumnName(metaData, i).toLowerCase();
				Object value = JdbcUtils.getResultSetValue(rs, i);

				// 查找匹配的字段
				Field field = fieldMap.get(columnName.replaceAll("_","").toLowerCase());
				if (field != null) {
					// 处理类型转换
					Object convertedValue = convertValue(value, field.getType());
					field.set(obj, convertedValue);
				}
			}
			return obj;
		} catch (Exception e) {
			throw new SQLException("Error mapping row to " + targetClass.getName(), e);
		}
	}

	/**
	 * 值类型转换
	 */
	private Object convertValue(Object value, Class<?> targetType) {
		if (value == null) return null;

		// 基本类型处理
		if (targetType == int.class || targetType == Integer.class) {
			return ((Number) value).intValue();
		} else if (targetType == long.class || targetType == Long.class) {
			return ((Number) value).longValue();
		} else if (targetType == double.class || targetType == Double.class) {
			return ((Number) value).doubleValue();
		} else if (targetType == float.class || targetType == Float.class) {
			return ((Number) value).floatValue();
		} else if (targetType == boolean.class || targetType == Boolean.class) {
			return Boolean.parseBoolean(value.toString());
		} else if (targetType == Status.class) {
			return Status.fromCode(Integer.parseInt(value.toString()));
		} else if (targetType == LocalDateTime.class) {
			if (value instanceof Timestamp) {
				return ((Timestamp) value).toLocalDateTime();
			}
		}

		// 其他类型直接返回
		return value;
	}
}