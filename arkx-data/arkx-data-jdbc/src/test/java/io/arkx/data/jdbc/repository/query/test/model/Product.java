package io.arkx.data.jdbc.repository.query.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

/**
 * @author Nobody
 * @date 2025-07-17 1:00
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("PRODUCTS")
public class Product {

	@Id
	private Long id;
	private String name;
	private String description;
	private BigDecimal price = new BigDecimal("0");
	private String category;
	private boolean active;

}