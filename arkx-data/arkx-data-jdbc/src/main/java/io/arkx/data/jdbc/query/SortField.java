package io.arkx.data.jdbc.query;

import lombok.Data;

/**
 * 排序字段DTO
 */
@Data
public  class SortField {
	private String field;     // 字段名
	private String direction; // 排序方向(ASC/DESC)

	public SortField() {}

	public SortField(String field, String direction) {
		this.field = field;
		this.direction = direction;
	}

}
