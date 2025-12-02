package io.arkx.data.lightning.sample.dto;

/**
 * @author Nobody
 * @date 2025-07-17 1:01
 * @since 1.0
 */

import java.math.BigDecimal;

import io.arkx.data.lightning.sample.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

	private Long id;

	private String name;

	private String description;

	private BigDecimal price;

	private String category;

	private boolean active;

	// 转换实体到 DTO
	public static ProductDto fromEntity(Product product) {
		return new ProductDto(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
				product.getCategory(), product.isActive());
	}

}
