package io.arkx.data.jdbc.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class QueryStats {
	private long durationMs; // 查询耗时(毫秒)
	private int queryCount; // 查询数量
	private int resultSize; // 结果大小(字符数)
}
