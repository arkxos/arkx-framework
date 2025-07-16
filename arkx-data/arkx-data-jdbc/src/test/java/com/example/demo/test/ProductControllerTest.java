package com.example.demo.test;

/**
 * @author Nobody
 * @date 2025-07-17 1:06
 * @since 1.0
 */

import com.example.demo.controller.ProductController;
import com.example.demo.dto.ProductDTO;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ProductService productService;

	// 测试创建产品（POST）
	@Test
	void createProduct_ShouldReturnCreated() throws Exception {
		ProductDTO inputDTO = new ProductDTO(null, "Test", "Desc", 99.99, "Cat", true);
		ProductDTO outputDTO = new ProductDTO(1L, "Test", "Desc", 99.99, "Cat", true);

		when(productService.createProduct(inputDTO)).thenReturn(outputDTO);

		mockMvc.perform(post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(inputDTO)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("Test"));
	}

	// 测试获取单个产品（GET /{id}）
	@Test
	void getProductById_ExistingId_ShouldReturnProduct() throws Exception {
		ProductDTO productDTO = new ProductDTO(1L, "Test", "Desc", 99.99, "Cat", true);

		when(productService.getProductById(1L)).thenReturn(productDTO);

		mockMvc.perform(get("/api/products/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("Test"));
	}

	// 测试获取单个产品（ID不存在）
	@Test
	void getProductById_NotExistingId_ShouldReturn404() throws Exception {
		when(productService.getProductById(999L)).thenThrow(new RuntimeException("Product not found"));

		mockMvc.perform(get("/api/products/999")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	// 测试更新产品（PUT /{id}）
	@Test
	void updateProduct_ExistingId_ShouldReturnUpdated() throws Exception {
		ProductDTO inputDTO = new ProductDTO(1L, "Updated", "New Desc", 100.0, "New Cat", true);
		ProductDTO outputDTO = new ProductDTO(1L, "Updated", "New Desc", 100.0, "New Cat", true);

		when(productService.updateProduct(1L, inputDTO)).thenReturn(outputDTO);

		mockMvc.perform(put("/api/products/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(inputDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated"))
				.andExpect(jsonPath("$.category").value("New Cat"));
	}

	// 测试删除产品（DELETE /{id}）
	@Test
	void deleteProduct_ExistingId_ShouldReturn204() throws Exception {
		doNothing().when(productService).deleteProduct(1L);

		mockMvc.perform(delete("/api/products/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	// 测试分页查询（带条件）
	@Test
	void getAllProducts_WithPagination_ShouldReturnPage() throws Exception {
		// 构造模拟分页数据
		ProductDTO p1 = new ProductDTO(1L, "Apple", "Fruit", 5.99, "Fruit", true);
		ProductDTO p2 = new ProductDTO(2L, "Banana", "Fruit", 3.99, "Fruit", true);
		List<ProductDTO> content = List.of(p1, p2);
		Page<ProductDTO> mockPage = new PageImpl<>(content, PageRequest.of(0, 2), 2);

		when(productService.getAllProducts(any(Pageable.class))).thenReturn(mockPage);

		mockMvc.perform(get("/api/products")
						.param("page", "0")
						.param("size", "2")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.size()").value(2))
				.andExpect(jsonPath("$.content[0].name").value("Apple"));
	}
}