package com.example.demo.service;

/**
 * @author Nobody
 * @date 2025-07-17 1:02
 * @since 1.0
 */

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	// 创建产品
	public ProductDTO createProduct(ProductDTO productDTO) {
		Product product = Product.builder()
				.name(productDTO.getName())
				.description(productDTO.getDescription())
				.price(productDTO.getPrice())
				.category(productDTO.getCategory())
				.active(productDTO.isActive())
				.build();

		Product savedProduct = productRepository.save(product);
		return ProductDTO.fromEntity(savedProduct);
	}

	// 获取所有产品（分页）
	public Page<ProductDTO> getAllProducts(Pageable pageable) {
		return productRepository.findAll(pageable)
				.map(ProductDTO::fromEntity);
	}

	// 按名称和类别筛选（分页）
	public Page<ProductDTO> getProductsByNameAndCategory(String name, String category, Pageable pageable) {
		return productRepository.findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(
						name, category, pageable)
				.map(ProductDTO::fromEntity);
	}

	// 按价格范围和活跃状态筛选（分页）
//	public Page<ProductDTO> getProductsByPriceRangeAndActive(double minPrice, double maxPrice, boolean active, Pageable pageable) {
//		return productRepository.findByPriceRangeAndActive(minPrice, maxPrice, active, pageable)
//				.map(ProductDTO::fromEntity);
//	}

	// 获取单个产品
	public ProductDTO getProductById(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
		return ProductDTO.fromEntity(product);
	}

	// 更新产品
	public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
		Product existingProduct = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

		existingProduct.setName(productDTO.getName());
		existingProduct.setDescription(productDTO.getDescription());
		existingProduct.setPrice(productDTO.getPrice());
		existingProduct.setCategory(productDTO.getCategory());
		existingProduct.setActive(productDTO.isActive());

		Product updatedProduct = productRepository.save(existingProduct);
		return ProductDTO.fromEntity(updatedProduct);
	}

	// 删除产品
	public void deleteProduct(Long id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
		} else {
			throw new RuntimeException("Product not found with id: " + id);
		}
	}
}
