package io.arkx.data.lightning.sample.controller;

/**
 * @author Nobody
 * @date 2025-07-17 1:02
 * @since 1.0
 */

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.arkx.data.lightning.sample.dto.ProductDto;
import io.arkx.data.lightning.sample.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 创建产品
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        ProductDto createdProduct = productService.createProduct(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // 获取所有产品（分页）
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(@RequestParam Optional<String> name,
            @RequestParam Optional<String> category, @RequestParam Optional<Double> minPrice,
            @RequestParam Optional<Double> maxPrice, @RequestParam Optional<Boolean> active,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<ProductDto> products;

        // 根据查询参数决定使用哪种查询方法
        if (name.isPresent() && category.isPresent()) {
            products = productService.getProductsByNameAndCategory(name.get(), category.get(), pageable);
            // } else if (minPrice.isPresent() && maxPrice.isPresent() &&
            // active.isPresent()) {
            // products = productService.getProductsByPriceRangeAndActive(
            // minPrice.get(), maxPrice.get(), active.get(), pageable);
        } else {
            products = productService.getAllProducts(pageable);
        }

        return ResponseEntity.ok(products);
    }

    // 获取单个产品
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // 更新产品
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    // 删除产品
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
