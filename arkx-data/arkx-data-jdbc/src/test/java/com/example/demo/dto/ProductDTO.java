package com.example.demo.dto;

/**
 * @author Nobody
 * @date 2025-07-17 1:01
 * @since 1.0
 */

import com.example.demo.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
	private Long id;
	private String name;
	private String description;
	private Double price;
	private String category;
	private boolean active;

	// 转换实体到 DTO
	public static ProductDTO fromEntity(Product product) {
		return new ProductDTO(
				product.getId(),
				product.getName(),
				product.getDescription(),
				product.getPrice(),
				product.getCategory(),
				product.isActive()
		);
	}
}