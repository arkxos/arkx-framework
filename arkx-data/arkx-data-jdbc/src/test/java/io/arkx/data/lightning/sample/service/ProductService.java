package io.arkx.data.lightning.sample.service;

/**
 * @author Nobody
 * @date 2025-07-17 1:02
 * @since 1.0
 */

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.arkx.data.lightning.sample.dto.ProductDto;
import io.arkx.data.lightning.sample.model.Product;
import io.arkx.data.lightning.sample.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	// 创建产品
	public ProductDto createProduct(ProductDto productDto) {
		Product product = Product.builder()
			.name(productDto.getName())
			.description(productDto.getDescription())
			.price(productDto.getPrice())
			.category(productDto.getCategory())
			.active(productDto.isActive())
			.build();

		Product savedProduct = productRepository.save(product);
		return ProductDto.fromEntity(savedProduct);
	}

	// 获取所有产品（分页）
	public Page<ProductDto> getAllProducts(Pageable pageable) {
		return productRepository.findAll(pageable).map(ProductDto::fromEntity);
	}

	// 按名称和类别筛选（分页）
	public Page<ProductDto> getProductsByNameAndCategory(String name, String category, Pageable pageable) {
		return productRepository.findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(name, category, pageable)
			.map(ProductDto::fromEntity);
	}

	// 按价格范围和活跃状态筛选（分页）
	// public Page<ProductDTO> getProductsByPriceRangeAndActive(double minPrice,
	// double maxPrice, boolean active, Pageable pageable) {
	// return productRepository.findByPriceRangeAndActive(minPrice, maxPrice,
	// active, pageable)
	// .map(ProductDTO::fromEntity);
	// }

	// 获取单个产品
	public ProductDto getProductById(Long id) {
		Product product = productRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
		return ProductDto.fromEntity(product);
	}

	// 更新产品
	public ProductDto updateProduct(Long id, ProductDto productDto) {
		Product existingProduct = productRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

		existingProduct.setName(productDto.getName());
		existingProduct.setDescription(productDto.getDescription());
		existingProduct.setPrice(productDto.getPrice());
		existingProduct.setCategory(productDto.getCategory());
		existingProduct.setActive(productDto.isActive());

		Product updatedProduct = productRepository.save(existingProduct);
		return ProductDto.fromEntity(updatedProduct);
	}

	// 删除产品
	public void deleteProduct(Long id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
		}
		else {
			throw new RuntimeException("Product not found with id: " + id);
		}
	}

}
