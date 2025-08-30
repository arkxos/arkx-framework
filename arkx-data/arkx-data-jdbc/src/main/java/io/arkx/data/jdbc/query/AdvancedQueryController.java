package io.arkx.data.jdbc.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.arkx.framework.commons.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
public class AdvancedQueryController {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

	/**
	 * 通用高级查询接口
	 *
	 * @param request 查询请求
	 * @return 生成的SQL语句
	 */
	@PostMapping("/api/advanced-query")
	public QueryResult advancedQuery(@RequestBody AdvancedQueryRequest request) {
		try {
			// 1. 验证输入参数
			validateRequest(request);

			// 2. 解析条件规则
			Rule rule = objectMapper.readValue(request.getRuleJson(), Rule.class);
			String whereClause = rule.toSql();

			// 3. 构建基础SQL
			String baseSql = buildBaseSql(request);

			// 4. 添加WHERE条件
			String fullSql = addWhereCondition(baseSql, whereClause);

			// 5. 添加排序
			fullSql = addOrderBy(fullSql, request.getSortFields());

			// 6. 添加分页
			fullSql = addPagination(fullSql, request.getPageNum(), request.getPageSize());

			return new QueryResult(fullSql);
		} catch (Exception e) {
			throw new RuntimeException("执行高级查询失败", e);
		}
	}

	private void validateRequest(AdvancedQueryRequest request) {
		if (request.getTableName() != null && !isValidIdentifier(request.getTableName())) {
			throw new IllegalArgumentException("表名包含非法字符");
		}

		if (request.getFields() != null) {
			for (String field : request.getFields()) {
				if (!isValidIdentifier(field)) {
					throw new IllegalArgumentException("字段名包含非法字符: " + field);
				}
			}
		}

		if (request.getSortFields() != null) {
			for (SortField sortField : request.getSortFields()) {
				if (!isValidIdentifier(sortField.getField())) {
					throw new IllegalArgumentException("排序字段包含非法字符: " + sortField.getField());
				}
			}
		}
	}

	private boolean isValidIdentifier(String identifier) {
		return SQL_INJECTION_PATTERN.matcher(identifier).matches();
	}

	private String buildBaseSql(AdvancedQueryRequest request) {
		if (request.getBaseSql() != null) {
			return request.getBaseSql();
		}

		if (request.getTableName() == null) {
			throw new IllegalArgumentException("必须提供tableName或baseSql参数");
		}

		String fields = "*";
		if (request.getFields() != null && !request.getFields().isEmpty()) {
			fields = String.join(", ", request.getFields());
		}

		return String.format("SELECT %s FROM %s", fields, request.getTableName());
	}

	private String addWhereCondition(String sql, String whereClause) {
		if (StringUtils.isEmpty(whereClause)) {
			return sql;
		}

		String upperSql = sql.toUpperCase();
		if (upperSql.contains(" WHERE ")) {
			return sql + " AND " + whereClause;
		} else {
			return sql + " WHERE " + whereClause;
		}
	}

	private String addOrderBy(String sql, List<SortField> sortFields) {
		if (sortFields == null || sortFields.isEmpty()) {
			return sql;
		}

		String orderByClause = sortFields.stream()
				.map(field -> field.getField() + " " + field.getDirection())
				.collect(Collectors.joining(", "));

		return sql + " ORDER BY " + orderByClause;
	}

	private String addPagination(String sql, Integer pageNum, Integer pageSize) {
		if (pageNum == null || pageSize == null) {
			return sql;
		}

		int offset = (pageNum - 1) * pageSize;
		return String.format("%s LIMIT %d OFFSET %d", sql, pageSize, offset);
	}



}
