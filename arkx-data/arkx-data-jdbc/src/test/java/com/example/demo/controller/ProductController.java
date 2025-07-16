package com.example.demo.controller;

/**
 * @author Nobody
 * @date 2025-07-17 1:02
 * @since 1.0
 */

import com.example.demo.dto.ProductDTO;
import com.example.demo.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	// 创建产品
	@PostMapping
	public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
		ProductDTO createdProduct = productService.createProduct(productDTO);
		return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
	}

	// 获取所有产品（分页）
	@GetMapping
	public ResponseEntity<Page<ProductDTO>> getAllProducts(
			@RequestParam Optional<String> name,
			@RequestParam Optional<String> category,
			@RequestParam Optional<Double> minPrice,
			@RequestParam Optional<Double> maxPrice,
			@RequestParam Optional<Boolean> active,
			@PageableDefault(page = 0, size = 10) Pageable pageable) {

		Page<ProductDTO> products;

		// 根据查询参数决定使用哪种查询方法
		if (name.isPresent() && category.isPresent()) {
			products = productService.getProductsByNameAndCategory(
					name.get(), category.get(), pageable);
//		} else if (minPrice.isPresent() && maxPrice.isPresent() && active.isPresent()) {
//			products = productService.getProductsByPriceRangeAndActive(
//					minPrice.get(), maxPrice.get(), active.get(), pageable);
		} else {
			products = productService.getAllProducts(pageable);
		}

		return ResponseEntity.ok(products);
	}

	// 获取单个产品
	@GetMapping("/{id}")
	public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
		ProductDTO product = productService.getProductById(id);
		return ResponseEntity.ok(product);
	}

	// 更新产品
	@PutMapping("/{id}")
	public ResponseEntity<ProductDTO> updateProduct(
			@PathVariable Long id,
			@RequestBody ProductDTO productDTO) {
		ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
		return ResponseEntity.ok(updatedProduct);
	}

	// 删除产品
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
