package io.arkx.data.lightning.query;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 高级查询请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvancedQueryRequest {

    private String dataSource; // 数据源名称

    private String tableName; // 表名

    private String baseSql; // 基础SQL

    private Rule rule; // 查询规则对象

    private List<String> fields; // 查询字段

    private List<SortField> sortFields; // 排序字段

    @Min(1)
    private Integer pageNum; // 页码

    @Min(1)
    private Integer pageSize; // 每页大小

    private Long timeout; // 查询超时(毫秒)

    private FormatOptions formatOptions; // 格式化选项

    private List<AdvancedQueryRequest> batchQueries; // 批量查询列表

    @Override
    public int hashCode() {
        return Objects.hash(dataSource, tableName, baseSql, rule, fields, sortFields, pageNum, pageSize, timeout,
                formatOptions, batchQueries);
    }

}
