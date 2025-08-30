package io.arkx.data.jdbc.query;

public class QueryResult {
	private String sql; // 生成的SQL语句

	public QueryResult(String sql) {
		this.sql = sql;
	}

	// getter
	public String getSql() {
		return sql;
	}
}
