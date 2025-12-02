package io.arkx.data.lightning.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.arkx.framework.commons.util.StringUtils;

import jakarta.validation.constraints.NotNull;

@RestController
public class AdvancedQueryController {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    /**
     * 通用高级查询接口
     *
     * @param request
     *            查询请求
     * @return 生成的SQL语句
     */
    @PostMapping("/api/advanced-query")
    public QueryResponse advancedQuery(@RequestBody @NotNull AdvancedQueryRequest request) {
        long startTime = System.nanoTime();

        try {
            // 1. 验证输入参数
            validateRequest(request);

            // 3. 处理批量查询
            if (request.getBatchQueries() != null && !request.getBatchQueries().isEmpty()) {
                return handleBatchQueries(request);
            }

            // 4. 执行单个查询
            QueryResult result = executeSingleQuery(request);

            // 5. 记录查询统计
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            return new QueryResponse(true, "查询成功", Arrays.asList(result),
                    new QueryStats(duration, 1, result.getSql().length()));
        } catch (Exception e) {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            return new QueryResponse(false, e.getMessage(), Collections.emptyList(), new QueryStats(duration, 0, 0));
        }
    }

    private QueryResponse handleBatchQueries(AdvancedQueryRequest request) {
        List<QueryResult> results = new ArrayList<>();
        long totalDuration = 0;
        int totalQueries = 0;
        int totalSize = 0;

        for (AdvancedQueryRequest subRequest : request.getBatchQueries()) {
            long startTime = System.nanoTime();
            try {
                QueryResult result = executeSingleQuery(subRequest);
                results.add(result);
                totalQueries++;
                totalSize += result.getSql().length();
            } catch (Exception e) {
                results.add(new QueryResult(null, e.getMessage()));
            } finally {
                totalDuration += TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            }
        }

        return new QueryResponse(true, "批量查询完成", results, new QueryStats(totalDuration, totalQueries, totalSize));
    }

    private QueryResult executeSingleQuery(AdvancedQueryRequest request) {
        // 1. 构建基础SQL
        String baseSql = buildBaseSql(request);

        // 2. 添加WHERE条件
        String whereClause = request.getRule() != null ? request.getRule().toSql() : "";
        String fullSql = addWhereCondition(baseSql, whereClause);

        // 3. 添加排序
        fullSql = addOrderBy(fullSql, request.getSortFields());

        // 4. 添加分页
        fullSql = addPagination(fullSql, request.getPageNum(), request.getPageSize());

        // 5. 格式化结果
        String formattedSql = formatSql(fullSql, request.getFormatOptions());

        return new QueryResult(formattedSql, null);
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

        String orderByClause = sortFields.stream().map(field -> field.getField() + " " + field.getDirection())
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

    private String formatSql(String sql, FormatOptions options) {
        if (options == null) {
            return sql;
        }

        // 这里可以实现各种格式化逻辑，如美化SQL、添加注释等
        if (options.isPretty()) {
            // 简单的美化逻辑
            return sql.replaceAll("(?i)SELECT", "SELECT\n  ").replaceAll("(?i)FROM", "\nFROM\n  ")
                    .replaceAll("(?i)WHERE", "\nWHERE\n  ").replaceAll("(?i)ORDER BY", "\nORDER BY\n  ")
                    .replaceAll("(?i)LIMIT", "\nLIMIT\n  ");
        }
        return sql;
    }

}
