package io.arkx.data.jdbc.query;

import lombok.Data;

import java.util.List;

/**
 * 高级查询请求DTO
 */
@Data
public class AdvancedQueryRequest {

	private String tableName;      // 要查询的表名
	private String baseSql;        // 基础SQL语句(可选)
	private String ruleJson;       // 条件规则JSON
	private List<String> fields;   // 要查询的字段列表
	private List<SortField> sortFields; // 排序字段
	private Integer pageNum;       // 页码(从1开始)
	private Integer pageSize;      // 每页大小

	@Override
	public int hashCode() {
		// 用于缓存键的生成
		return (tableName + baseSql + ruleJson + fields + sortFields + pageNum + pageSize).hashCode();
	}
}
