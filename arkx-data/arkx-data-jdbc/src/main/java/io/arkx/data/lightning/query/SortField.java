package io.arkx.data.lightning.query;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排序字段DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortField {

	@NotNull
	private String field; // 字段名

	private String direction = "ASC";

	; // 排序方向(ASC/DESC)

}
