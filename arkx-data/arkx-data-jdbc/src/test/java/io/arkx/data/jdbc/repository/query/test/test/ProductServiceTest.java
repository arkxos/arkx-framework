package io.arkx.data.jdbc.repository.query.test.test;

/**
 * @author Nobody
 * @date 2025-07-17 1:05
 * @since 1.0
 */

import io.arkx.data.jdbc.repository.query.test.dto.ProductDTO;
import io.arkx.data.jdbc.repository.query.test.model.Product;
import io.arkx.data.jdbc.repository.query.test.repository.ProductRepository;
import io.arkx.data.jdbc.repository.query.test.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductService productService;

	// 测试创建产品
	@Test
	void createProduct_ShouldReturnSavedProduct() {
		ProductDTO inputDTO = new ProductDTO(null, "New Product", "Desc", new BigDecimal("99.99"), "Category", true);
		Product savedEntity = Product.builder()
				.id(1L)
				.name("New Product")
				.price(new BigDecimal("99.99"))
				.category("Category")
				.active(true)
				.build();

		when(productRepository.save(any(Product.class))).thenReturn(savedEntity);

		ProductDTO result = productService.createProduct(inputDTO);

		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("New Product");
		verify(productRepository, times(1)).save(any(Product.class));
	}

	// 测试获取单个产品（存在）
	@Test
	void getProductById_ExistingId_ShouldReturnProduct() {
		Long productId = 1L;
		Product entity = Product.builder().id(productId).name("Test").build();
		ProductDTO expectedDTO = ProductDTO.fromEntity(entity);

		when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

		ProductDTO result = productService.getProductById(productId);

		assertThat(result).isEqualTo(expectedDTO);
	}

	// 测试获取单个产品（不存在）
	@Test
	void getProductById_NotExistingId_ShouldThrowException() {
		Long productId = 999L;

		when(productRepository.findById(productId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productService.getProductById(productId))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Product not found with id: " + productId);
	}

	// 测试更新产品（存在）
	@Test
	void updateProduct_ExistingId_ShouldReturnUpdatedProduct() {
		Long productId = 1L;
		Product existingEntity = Product.builder()
				.id(productId)
				.name("Old Name")
				.price(new BigDecimal("50.0"))
				.active(false)
				.build();
		ProductDTO inputDTO = new ProductDTO(productId, "New Name", "New Desc", new BigDecimal("100.0"), "New Category", true);
		Product updatedEntity = Product.builder()
				.id(productId)
				.name("New Name")
				.price(new BigDecimal("100.0"))
				.category("New Category")
				.active(true)
				.build();
		ProductDTO expectedDTO = ProductDTO.fromEntity(updatedEntity);

		when(productRepository.findById(productId)).thenReturn(Optional.of(existingEntity));
		when(productRepository.save(any(Product.class))).thenReturn(updatedEntity);

		ProductDTO result = productService.updateProduct(productId, inputDTO);

		assertThat(result).isEqualTo(expectedDTO);
		verify(productRepository, times(1)).save(any(Product.class));
	}

	// 测试删除产品（存在）
	@Test
	void deleteProduct_ExistingId_ShouldDelete() {
		Long productId = 1L;

		when(productRepository.existsById(productId)).thenReturn(true);
		doNothing().when(productRepository).deleteById(productId);

		productService.deleteProduct(productId);

		verify(productRepository, times(1)).deleteById(productId);
	}

	// 测试分页查询所有产品
	@Test
	void getAllProducts_Pagination_ShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		List<Product> mockProducts = List.of(
				Product.builder().id(1L).name("Product1").build(),
				Product.builder().id(2L).name("Product2").build()
		);
		Page<Product> mockPage = new PageImpl<>(mockProducts, pageable, 2);

		when(productRepository.findAll(pageable)).thenReturn(mockPage);

		Page<ProductDTO> result = productService.getAllProducts(pageable);

		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getName()).isEqualTo("Product1");
	}
}